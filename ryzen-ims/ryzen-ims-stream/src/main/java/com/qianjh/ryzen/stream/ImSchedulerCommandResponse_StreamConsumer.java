package com.qianjh.ryzen.stream;

import com.qianjh.ryzen.entity.ImAccount;
import com.qianjh.ryzen.entity.SchedulerCommand;
import com.qianjh.ryzen.enums.ImAccountAssignStatus;
import com.qianjh.ryzen.enums.ImAccountWorkStatus;
import com.qianjh.ryzen.framework.common.dto.Operator;
import com.qianjh.ryzen.framework.common.util.GsonUtils;
import com.qianjh.ryzen.framework.stream.consumer.StreamConsumer;
import com.qianjh.ryzen.model.SchedulerCommandResponse;
import com.qianjh.ryzen.service.ImAccountService;
import com.qianjh.ryzen.service.SchedulerCommandService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Slf4j
@Component
@Configuration
@RequiredArgsConstructor
public class ImSchedulerCommandResponse_StreamConsumer implements StreamConsumer {

    private final ImAccountService imAccountService;

    @Bean
    public Consumer<Message<String>> imsSchedulerCommandResponseSource(SchedulerCommandService imAccountCommandService) {
        return message -> {
            String payload = message.getPayload();

            // operator
            Operator operator = Operator.builder()
                    .name("IMS_SCHEDULER")
                    .build();

            // command
            SchedulerCommandResponse response = GsonUtils.GSON.fromJson(payload, SchedulerCommandResponse.class);
            SchedulerCommand command = imAccountCommandService.getById(response.code());
            if (command == null) {
                log.error("指令不存在 ASSIGN ::: {}", message);
                return;
            }
            // command update
            imAccountCommandService.response(command, response.success(), response.errorMessage());

            switch (response.type()) {
                case ASSIGN -> {
                    ImAccount imAccount = imAccountService.getById(command.getImAccountId());
                    if (imAccount == null) {
                        log.error("ImAccount不存在 ASSIGN ::: {}", payload);
                        return;
                    }
                    // 更新状态与分配
                    if (response.success()) {
                        imAccountService.updateAssignStatus(imAccount, imAccount.getAssignStatus(), ImAccountAssignStatus.ASSIGNED, operator);
                        imAccountService.recordAssignment(imAccount, response.assignmentId());
                    } else { // 改回状态
                        imAccountService.updateAssignStatus(imAccount, imAccount.getAssignStatus(), ImAccountAssignStatus.NONE, operator);
                    }
                }
                case RELEASE -> {
                    ImAccount imAccount = imAccountService.getById(command.getImAccountId());
                    if (imAccount == null) {
                        log.error("ImAccount不存在 RELEASE ::: {}", payload);
                        return;
                    }
                    imAccountService.updateAssignStatus(imAccount, imAccount.getAssignStatus(), ImAccountAssignStatus.RELEASING, operator);
                    imAccountService.clearAssignment(imAccount);
                }
                case START -> {
                    ImAccount imAccount = imAccountService.getById(command.getImAccountId());
                    if (imAccount == null) {
                        log.error("ImAccount不存在 START ::: {}", payload);
                        return;
                    }
                    if (imAccount.getWorkStatus() != ImAccountWorkStatus.STARTING) {
                        log.error("ImAccount.workStatus != STARTING ::: {}", payload);
                        return;
                    }
                    ImAccountWorkStatus afterStatus = response.success() ? ImAccountWorkStatus.WAIT_LOGIN : ImAccountWorkStatus.NONE;
                    // 更新状态
                    imAccountService.updateWorkStatus(imAccount, imAccount.getWorkStatus(), afterStatus, operator);
                }
                case STOP -> {
                    ImAccount imAccount = imAccountService.getById(command.getImAccountId());
                    if (imAccount == null) {
                        log.error("ImAccount不存在 STOP ::: {}", payload);
                        return;
                    }
                    if (imAccount.getWorkStatus() != ImAccountWorkStatus.STOPPING) {
                        log.error("ImAccount.workStatus != STOPPING ::: {}", payload);
                        return;
                    }
                    // 更新状态
                    if (response.success()) {
                        imAccountService.updateWorkStatus(imAccount, imAccount.getWorkStatus(), ImAccountWorkStatus.STOPPED, operator);
                    }
                }
            }

        };
    }
}
