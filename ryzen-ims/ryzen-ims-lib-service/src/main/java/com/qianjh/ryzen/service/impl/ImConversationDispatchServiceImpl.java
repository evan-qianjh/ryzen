package com.qianjh.ryzen.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.qianjh.ryzen.entity.*;
import com.qianjh.ryzen.ims.enums.ImConversationDispatchType;
import com.qianjh.ryzen.ims.enums.ServiceAccountStatus;
import com.qianjh.ryzen.mapper.ServiceAccountMapper;
import com.qianjh.ryzen.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImConversationDispatchServiceImpl implements ImConversationDispatchService {
    private final ImAccountService imAccountService;
    private final ServiceAccountMapper serviceAccountMapper;
    private final ImConversationService imConversationService;
    private final ServiceAccountService serviceAccountService;
    private final MatchTagImAccountService matchTagImAccountService;
    private final MatchTagServiceAccountService matchTagServiceAccountService;
    private final ImConversationDispatchRecordService imConversationDispatchRecordService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ServiceAccount dispatch(ImConversation conversation) {
        Long lastServiceAccount = conversation.getServiceAccountId();

        // 匹配服务账户
        ImConversationDispatchType dispatchType = ImConversationDispatchType.MATCH;
        ServiceAccount serviceAccount = match(conversation);

        // 未匹配到服务账户
        if (serviceAccount == null) {
            dispatchType = ImConversationDispatchType.FALLBACK;
//            serviceAccount = null; // TODO 设置兜底账户
        }

        // 没可用服务账号
        if (serviceAccount == null) {
            // TODO 考虑自动回复
            return null;
        }

        // 修改会话分配服务账户
        boolean success = imConversationService.updateServiceAccount(conversation, serviceAccount);
        Assert.isTrue(success, "会话分配账户异常");

        // 记录日志
        if (!Objects.equals(serviceAccount.getId(), lastServiceAccount)) {
            ImConversationDispatchRecord entity = imConversationDispatchRecordService.create(conversation, serviceAccount, dispatchType);
            log.info("会话调度 ::: record={}", entity);
        }

        return serviceAccount;
    }

    /**
     * 匹配
     *
     * @param conversation 对话
     * @return 服务账户
     */
    private ServiceAccount match(ImConversation conversation) {
        Long conversationId = conversation.getId();
        Long oemId = conversation.getOemId();
        Long tenantId = conversation.getTenantId();

        // 找出最后服务且在线的服务账号
        Long lastServiceAccountId = conversation.getServiceAccountId();
        if (lastServiceAccountId != null) {
            ServiceAccount serviceAccount = serviceAccountService.getById(oemId, tenantId, lastServiceAccountId);
            if (serviceAccount != null && serviceAccount.getOnline()) {
                log.info("分配最近一次服务且在线的服务账号 ::: conversationId={}, serviceAccountId={}", conversationId, serviceAccount.getId());
                return serviceAccount;
            }
        }

        // 会话对应的IM账号
        ImAccount imAccount = imAccountService.getById(oemId, tenantId, conversation.getImAccountId());
        if (imAccount == null) {
            log.error("异常对话-没有IM账户 ::: conversationId={}", conversationId);
            return null;
        }

        // 找出同IM类型在线的客服
        List<ServiceAccount> serviceAccounts = serviceAccountMapper.selectJoinList(new MPJLambdaWrapper<ServiceAccount>()
                .selectAll(ServiceAccount.class)
                // imType
                .innerJoin(ServiceAccountImType.class, (join) -> join
                        .eq(ServiceAccountImType::getServiceAccountId, ServiceAccount::getId)
                        .eq(ServiceAccountImType::getImType, imAccount.getImType())
                )
                //
                .eq(ServiceAccount::getOemId, oemId)
                .eq(ServiceAccount::getTenantId, tenantId)
                .eq(ServiceAccount::getStatus, ServiceAccountStatus.ENABLED)
                .eq(ServiceAccount::getOnline, true)
        );
        log.info("找出候选服务账户 ::: conversationId={}, serviceAccounts={}", conversationId, serviceAccounts.size());
        if (serviceAccounts.isEmpty()) {
            log.info("无可用服务账号 ::: conversationId={}, imType={}", conversationId, imAccount.getImType());
            return null;
        }
        Map<Long, ServiceAccount> serviceAccountMap = serviceAccounts.stream().collect(Collectors.toMap(ServiceAccount::getId, Function.identity()));

        // 查出这些客服的标签
        Map<Long, List<Long>> serviceAccountIdMatchTagIdsMap = matchTagServiceAccountService.list(new LambdaQueryWrapper<MatchTagServiceAccount>()
                .eq(MatchTagServiceAccount::getOemId, oemId)
                .eq(MatchTagServiceAccount::getTenantId, tenantId)
                .in(MatchTagServiceAccount::getId, serviceAccountMap.keySet()
                )
        ).stream().collect(Collectors.groupingBy(MatchTagServiceAccount::getServiceAccountId,
                Collectors.mapping(
                        MatchTagServiceAccount::getMatchTagId,
                        Collectors.toList()
                )));

        // IM账号标签
        Set<Long> imAccountMatchTagIds = matchTagImAccountService.listByImAccount(oemId, tenantId, imAccount.getId())
                .stream()
                .map(MatchTagImAccount::getMatchTagId)
                .collect(Collectors.toSet());

        // 匹配
        Long matchedServiceAccountId = match(imAccountMatchTagIds, serviceAccountIdMatchTagIdsMap);

        // 匹配到合适的
        if (matchedServiceAccountId != null) {
            log.info("通过标签匹配服务账号-命中 ::: conversationId={}, imAccountMatchTagIds={}, serviceAccountIdMatchTagIdsMap={}",
                    conversationId, imAccountMatchTagIds, serviceAccountIdMatchTagIdsMap);
            return serviceAccountMap.get(matchedServiceAccountId);
        }
        log.info("通过标签匹配服务账号-未命中 ::: conversationId={}, imAccountMatchTagIds={}, serviceAccountIdMatchTagIdsMap={}",
                conversationId, imAccountMatchTagIds, serviceAccountIdMatchTagIdsMap);


        // TODO 根据服务账号当前负载能力->负载均衡

        // 随机
        Collections.shuffle(serviceAccounts);
        ServiceAccount serviceAccount = serviceAccounts.getFirst();
        log.info("从候选服务账号中随机分配一个 ::: conversationId={}, serviceAccountId={}", conversationId, serviceAccount.getId());
        return serviceAccount;
    }

    /**
     * 匹配
     *
     * @param imAccountMatchTagIds           IM账号的匹配标签
     * @param serviceAccountIdMatchTagIdsMap 服务账号拥有的匹配标签
     * @return 匹配中的服务账号
     */
    private Long match(Set<Long> imAccountMatchTagIds, Map<Long, List<Long>> serviceAccountIdMatchTagIdsMap) {
        // 算法-必须全部标签匹配？
        // 算法-任意标签匹配即可？

        // 算法-匹配标签最多的优先: 遍历所有服务账户，计算每个客服与IM账号匹配的标签数量，最后返回匹配标签数量最多的服务账户ID。
        return serviceAccountIdMatchTagIdsMap.entrySet().stream()
                .map(entry -> Map.entry(
                        entry.getKey(),
                        entry.getValue().stream()
                                .filter(imAccountMatchTagIds::contains)
                                .count()
                ))
                .filter(entry -> entry.getValue() > 0)
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }
}
