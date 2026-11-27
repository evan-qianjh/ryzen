package com.qianjh.ryzen.stream.subscriber;

import com.qianjh.ryzen.entity.AppToSchedulerTask;
import com.qianjh.ryzen.entity.ImMessage;
import com.qianjh.ryzen.framework.common.util.IdUtils;
import com.qianjh.ryzen.ims.enums.AppToSchedulerTaskStatus;
import com.qianjh.ryzen.ims.enums.SchedulerToAppTaskResponseResult;
import com.qianjh.ryzen.ims.stream.scheduler_to_app.task.SchedulerToAppTaskResponse;
import com.qianjh.ryzen.service.AppToSchedulerTaskService;
import com.qianjh.ryzen.service.ImMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.slf4j.event.Level;
import org.springframework.stereotype.Component;

/**
 * @author QianJH
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SchedulerToAppTaskResponseSubscriberImpl implements SchedulerToAppTaskResponse.Subscriber {

    private final AppToSchedulerTaskService appToSchedulerTaskService;
    private final ImMessageService imMessageService;

    @Override
    public void subscribe(SchedulerToAppTaskResponse.Body body) {
        log.info("消费 SchedulerToAppTaskResponse ::: {}", body);

        AppToSchedulerTask task = appToSchedulerTaskService.getById(IdUtils.ofString(body.code()));
        if (task == null) {
            log.error("不存在的AppToSchedulerTask ::: body={}", body);
            return;
        }

        // 转换状态
        AppToSchedulerTaskStatus afterStatus = convertStatus(body.result());
        if (afterStatus != null) {
            boolean success = appToSchedulerTaskService.updateStatus(task, task.getStatus(), afterStatus,
                    body.imReceiptCode(), body.errorMessage(), null);
            log.atLevel(success ? Level.DEBUG : Level.ERROR).log("AppToSchedulerTask修改状态 ::: body={}, success={}", body, success);

            // 关闭
            if (afterStatus.isClosed()) {
                if (AppToSchedulerTaskStatus.SUCCESS == afterStatus) {
                    // 通过回执预生成一条消息
                    ImMessage imMessage = imMessageService.createByReceiptIfAbsent(task, body.imReceiptCode());
                    log.info("收到发送回执[成功]，创建消息 ::: {}", imMessage);
                } else {
                    // TODO ws通知失败？
                    log.warn("推送ws MESSAGE DELETE");
                }
            }
        }
    }

    private static @Nullable AppToSchedulerTaskStatus convertStatus(SchedulerToAppTaskResponseResult result) {
        switch (result) {
            case ACCEPTED -> {
                return AppToSchedulerTaskStatus.ACCEPTED;
            }
            case DUPLICATE -> {
                return AppToSchedulerTaskStatus.REJECTED;
            }
            case SUCCESS -> {
                return AppToSchedulerTaskStatus.SUCCESS;
            }
            case FAILED -> {
                return AppToSchedulerTaskStatus.FAILED;
            }
            case UNKNOWN -> {
                return AppToSchedulerTaskStatus.UNKNOWN;
            }
            case TIMEOUT -> {
                return AppToSchedulerTaskStatus.TIMEOUT;
            }
        }
        return null;
    }
}
