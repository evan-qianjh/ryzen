package com.qianjh.ryzen.stream.subscriber;

import com.qianjh.ryzen.entity.ImAccount;
import com.qianjh.ryzen.entity.SchedulerCommand;
import com.qianjh.ryzen.framework.common.dto.Operator;
import com.qianjh.ryzen.ims.enums.ImAccountAssignStatus;
import com.qianjh.ryzen.ims.enums.ImAccountExecutorStatus;
import com.qianjh.ryzen.ims.stream.scheduler_to_app.command.SchedulerToAppCommandResponse;
import com.qianjh.ryzen.service.ImAccountService;
import com.qianjh.ryzen.service.SchedulerCommandService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author QianJH
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SchedulerToAppCommandResponseSubscriberImpl implements SchedulerToAppCommandResponse.Subscriber {
    private final SchedulerCommandService schedulerCommandService;
    private final ImAccountService imAccountService;

    @Override
    public void subscribe(SchedulerToAppCommandResponse.Body body) {
        log.debug("消费SchedulerToAppCommandResponse ::: {}", body);

        // operator
        Operator operator = Operator.builder()
                .name("IMS_SCHEDULER")
                .build();

        // command
        SchedulerCommand command = schedulerCommandService.getById(body.code());
        if (command == null) {
            log.error("指令不存在 ASSIGN ::: {}", body);
            return;
        }
        // command update
        schedulerCommandService.response(command, body.success(), body.errorMessage());

        switch (body.type()) {
            case ASSIGN_EXECUTOR -> {
                ImAccount imAccount = imAccountService.getById(command.getDomainId());
                if (imAccount == null) {
                    log.error("ImAccount不存在 ASSIGN ::: {}", body);
                    return;
                }
                // 更新状态与分配
                if (body.success()) {
                    imAccountService.updateAssignStatus(imAccount, imAccount.getAssignStatus(), ImAccountAssignStatus.ASSIGNED, operator);
                    imAccountService.recordAssignment(imAccount, body.assignmentId());
                } else { // 改回状态
                    imAccountService.updateAssignStatus(imAccount, imAccount.getAssignStatus(), ImAccountAssignStatus.NONE, operator);
                }
            }
            case RELEASE_EXECUTOR -> {
                ImAccount imAccount = imAccountService.getById(command.getDomainId());
                if (imAccount == null) {
                    log.error("ImAccount不存在 RELEASE ::: {}", body);
                    return;
                }
                if (body.success()) {
                    imAccountService.updateAssignStatus(imAccount, imAccount.getAssignStatus(), ImAccountAssignStatus.RELEASED, operator);
                    imAccountService.clearAssignment(imAccount);
                }
            }
            case START_EXECUTOR -> {
                ImAccount imAccount = imAccountService.getById(command.getDomainId());
                if (imAccount == null) {
                    log.error("ImAccount不存在 START ::: {}", body);
                    return;
                }
                if (imAccount.getExecutorStatus() != ImAccountExecutorStatus.PENDING_ACCEPT_START) {
                    log.error("ImAccount.workStatus != PENDING_ACCEPT_START ::: {}", body);
                    return;
                }
                ImAccountExecutorStatus afterStatus = body.success() ? ImAccountExecutorStatus.PENDING_START : ImAccountExecutorStatus.NONE;
                // 更新状态
                imAccountService.updateExecutorStatus(imAccount, imAccount.getExecutorStatus(), afterStatus, operator);
            }
            case STOP_EXECUTOR -> {
                ImAccount imAccount = imAccountService.getById(command.getDomainId());
                if (imAccount == null) {
                    log.error("ImAccount不存在 STOP ::: {}", body);
                    return;
                }
                if (imAccount.getExecutorStatus() != ImAccountExecutorStatus.STOPPING) {
                    log.error("ImAccount.workStatus != STOPPING ::: {}", body);
                    return;
                }
                // 更新状态
                if (body.success()) {
                    imAccountService.updateExecutorStatus(imAccount, imAccount.getExecutorStatus(), ImAccountExecutorStatus.STOPPED, operator);
                }
            }
        }
    }
}
