package com.qianjh.ryzen.stream.subscriber;

import com.qianjh.ryzen.entity.AppToSchedulerTask;
import com.qianjh.ryzen.entity.ImAccount;
import com.qianjh.ryzen.entity.ImConversation;
import com.qianjh.ryzen.ims.enums.MessageDirection;
import com.qianjh.ryzen.ims.stream.im_message.im_message.AppToSchedulerTaskCreated;
import com.qianjh.ryzen.service.AppToSchedulerTaskService;
import com.qianjh.ryzen.service.ImAccountService;
import com.qianjh.ryzen.service.ImConversationService;
import com.qianjh.ryzen.service.MatchTagImAccountService;
import com.qianjh.ryzen.service.dto.MatchTagImAccountWithMatchTagView;
import com.qianjh.ryzen.tenant.stream.topic.from_ims.domain.push_message.type.PushMessageInsert;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author QianJH
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AppToSchedulerTaskCreatedSubscriberImpl implements AppToSchedulerTaskCreated.Subscriber {

    private final MatchTagImAccountService matchTagImAccountService;
    private final AppToSchedulerTaskService appToSchedulerTaskService;
    private final ImConversationService imConversationService;
    private final ImAccountService imAccountService;
    private final PushMessageInsert.Publisher pushMessageInsertPublisher;

    @Override
    public void subscribe(AppToSchedulerTaskCreated.Body body) {
        log.debug("消费 AppToSchedulerTaskCreated ::: {}", body);
        Long oemId = body.getOemId();
        Long tenantId = body.getTenantId();

        AppToSchedulerTask task = appToSchedulerTaskService.getById(oemId, tenantId, body.getId());
        if (task == null) {
            log.error("不存在的AppToSchedulerTask ::: body={}", body);
            return;
        }

        ImConversation imConversation = imConversationService.getById(oemId, tenantId, task.getConversationId());
        if (imConversation == null) {
            log.error("不存在的ImConversation ::: {}", body);
            return;
        }

        ImAccount imAccount = imAccountService.getById(oemId, tenantId, imConversation.getImAccountId());
        if (imAccount == null) {
            log.error("不存在的ImAccount ::: {}", body);
            return;
        }

        // 对话标签
        List<String> matchTags = matchTagImAccountService.getByImAccountWithMatchTag(oemId, tenantId, imAccount.getId())
                .stream().map(MatchTagImAccountWithMatchTagView::getTag).toList();

        // build conversation
        PushMessageInsert.Body.Conversation conversation = PushMessageInsert.Body.Conversation.builder()
                .id(imConversation.getId())
                .imType(imAccount.getImType().name())
                .title(imConversation.getTitle())
                .subTitle(imConversation.getSubTitle())
                .avatar(imConversation.getAvatar())
                .matchTags(matchTags)
                .serviceAccountId(task.getServiceAccountId())
                .build();

        // build message
        PushMessageInsert.Body message = PushMessageInsert.Body.builder()
                .oemId(oemId)
                .tenantId(tenantId)
                .id(task.getId())
                .seq(null)
                .direction(MessageDirection.OUTBOUND.name())
                .senderName(null)
                .senderAvatar(null)
                .content(task.getContent())
                .sentTime(null)
                .conversation(conversation)
                .version(0L)
                .preview(true)
                .build();
        pushMessageInsertPublisher.publish(message);

        log.info("任务创建，推送ws消息-INSERT ::: taskId={}", task.getId());
    }
}
