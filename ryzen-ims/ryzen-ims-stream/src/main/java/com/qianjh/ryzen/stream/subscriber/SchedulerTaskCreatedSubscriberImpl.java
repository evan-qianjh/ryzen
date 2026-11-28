package com.qianjh.ryzen.stream.subscriber;

import com.qianjh.ryzen.entity.ImAccount;
import com.qianjh.ryzen.entity.ImConversation;
import com.qianjh.ryzen.entity.SchedulerTask;
import com.qianjh.ryzen.ims.enums.MessageDirection;
import com.qianjh.ryzen.ims.stream.scheduler.scheduler_task.SchedulerTaskCreated;
import com.qianjh.ryzen.service.ImAccountService;
import com.qianjh.ryzen.service.ImConversationService;
import com.qianjh.ryzen.service.MatchTagImAccountService;
import com.qianjh.ryzen.service.SchedulerTaskService;
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
public class SchedulerTaskCreatedSubscriberImpl implements SchedulerTaskCreated.Subscriber {

    private final MatchTagImAccountService matchTagImAccountService;
    private final SchedulerTaskService schedulerTaskService;
    private final ImConversationService imConversationService;
    private final ImAccountService imAccountService;
    private final PushMessageInsert.Publisher pushMessageInsertPublisher;

    @Override
    public void subscribe(SchedulerTaskCreated.Body body) {
        log.debug("消费 SchedulerTaskCreated ::: {}", body);
        Long oemId = body.getOemId();
        Long tenantId = body.getTenantId();

        SchedulerTask task = schedulerTaskService.getById(oemId, tenantId, body.getId());
        if (task == null) {
            log.error("不存在的 SchedulerTask ::: body={}", body);
            return;
        }

        ImConversation imConversation = imConversationService.getById(oemId, tenantId, task.getConversationId());
        if (imConversation == null) {
            log.error("不存在的 ImConversation ::: {}", body);
            return;
        }

        ImAccount imAccount = imAccountService.getById(oemId, tenantId, imConversation.getImAccountId());
        if (imAccount == null) {
            log.error("不存在的 ImAccount ::: {}", body);
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

        log.debug("任务创建，推送ws消息-INSERT ::: taskId={}", task.getId());
    }
}
