package com.qianjh.ryzen.stream;

import com.qianjh.ryzen.entity.ImAccount;
import com.qianjh.ryzen.enums.ImAccountExecutorStatus;
import com.qianjh.ryzen.framework.common.util.GsonUtils;
import com.qianjh.ryzen.framework.stream.consumer.StreamConsumer;
import com.qianjh.ryzen.model.SchedulerReport;
import com.qianjh.ryzen.service.ImAccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Slf4j
@Component
@Configuration
@RequiredArgsConstructor
public class ImSchedulerReport_StreamConsumer implements StreamConsumer {

    private final ImAccountService imAccountService;

    @Bean
    public Consumer<Message<String>> imsSchedulerReportSource() {
        return message -> {
            MessageHeaders headers = message.getHeaders();
            String payload = message.getPayload();
            log.info("message ::: {}", message);


            SchedulerReport report = GsonUtils.GSON.fromJson(payload, SchedulerReport.class);
            switch (report.type()) {
                case EXECUTOR_STARTED -> {
                    ImAccount imAccount = imAccountService.getByAssignmentId(report.assignmentId());
                    if (imAccount == null) {
                        log.error("未找到指定指派的账号 EXECUTOR_STARTED ::: {}", payload);
                        return;
                    }
                    ImAccountExecutorStatus beforeStatus = imAccount.getExecutorStatus();
                    imAccountService.updateExecutorStatus(imAccount, beforeStatus, ImAccountExecutorStatus.STARTED, null);
                }
                case EXECUTOR_STOPPED -> {
                    ImAccount imAccount = imAccountService.getByAssignmentId(report.assignmentId());
                    if (imAccount == null) {
                        log.error("未找到指定指派的账号 EXECUTOR_STOPPED ::: {}", payload);
                        return;
                    }
                    ImAccountExecutorStatus beforeStatus = imAccount.getExecutorStatus();
                    imAccountService.updateExecutorStatus(imAccount, beforeStatus, ImAccountExecutorStatus.STOPPED, null);
                }
            }
        };
    }
}
