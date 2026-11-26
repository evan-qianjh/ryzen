package com.qianjh.ryzen.stream.subscriber;

import com.qianjh.ryzen.entity.ImAccount;
import com.qianjh.ryzen.ims.enums.ImAccountExecutorStatus;
import com.qianjh.ryzen.ims.stream.domain.report.type.SchedulerToAppReportEvent;
import com.qianjh.ryzen.service.ImAccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author QianJH
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SchedulerToAppReportEventSubscriberImpl implements SchedulerToAppReportEvent.Subscriber {

    private final ImAccountService imAccountService;

    @Override
    public void subscribe(SchedulerToAppReportEvent.Body body) {
        log.debug("消费 SchedulerToAppReportEvent ::: {}", body);

        switch (body.type()) {
            case EXECUTOR_STARTING -> {
                ImAccount imAccount = imAccountService.getByAssignmentId(body.assignmentId());
                if (imAccount == null) {
                    log.error("未找到指定指派的账号 EXECUTOR_STARTING ::: {}", body);
                    return;
                }
                ImAccountExecutorStatus beforeStatus = imAccount.getExecutorStatus();
                imAccountService.updateExecutorStatus(imAccount, beforeStatus, ImAccountExecutorStatus.STARTING, null);
            }
            case EXECUTOR_WAIT_LOGIN -> {
                ImAccount imAccount = imAccountService.getByAssignmentId(body.assignmentId());
                if (imAccount == null) {
                    log.error("未找到指定指派的账号 EXECUTOR_WAIT_LOGIN ::: {}", body);
                    return;
                }
                ImAccountExecutorStatus beforeStatus = imAccount.getExecutorStatus();
                imAccountService.updateExecutorStatus(imAccount, beforeStatus, ImAccountExecutorStatus.WAIT_LOGIN, null);
            }
            case EXECUTOR_STARTED -> {
                ImAccount imAccount = imAccountService.getByAssignmentId(body.assignmentId());
                if (imAccount == null) {
                    log.error("未找到指定指派的账号 EXECUTOR_STARTED ::: {}", body);
                    return;
                }
                ImAccountExecutorStatus beforeStatus = imAccount.getExecutorStatus();
                imAccountService.updateExecutorStatus(imAccount, beforeStatus, ImAccountExecutorStatus.STARTED, null);
            }
            case EXECUTOR_STOPPED -> {
                ImAccount imAccount = imAccountService.getByAssignmentId(body.assignmentId());
                if (imAccount == null) {
                    log.error("未找到指定指派的账号 EXECUTOR_STOPPED ::: {}", body);
                    return;
                }
                ImAccountExecutorStatus beforeStatus = imAccount.getExecutorStatus();
                imAccountService.updateExecutorStatus(imAccount, beforeStatus, ImAccountExecutorStatus.STOPPED, null);
            }
        }
    }
}
