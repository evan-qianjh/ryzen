package com.qianjh.ryzen.stream.subscriber;

import com.qianjh.ryzen.entity.AppToSchedulerTask;
import com.qianjh.ryzen.framework.common.util.IdUtils;
import com.qianjh.ryzen.ims.enums.AppToSchedulerTaskStatus;
import com.qianjh.ryzen.ims.enums.SchedulerToAppTaskResponseResult;
import com.qianjh.ryzen.ims.stream.scheduler_to_app.task.SchedulerToAppTaskResponse;
import com.qianjh.ryzen.service.AppToSchedulerTaskService;
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
            boolean success = appToSchedulerTaskService.updateStatus(task, task.getStatus(), afterStatus, body.errorMessage(), null);
            log.atLevel(success ? Level.DEBUG : Level.ERROR).log("AppToSchedulerTask修改状态 ::: body={}, success={}", body, success);

            // 关闭
            if (afterStatus.isClosed()) {
                if (AppToSchedulerTaskStatus.SUCCESS == afterStatus) {
                    // TODO ws通知成功
                    log.info("ws通知成功");
                } else {
                    // TODO ws通知失败
                    log.info("ws通知失败");
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
