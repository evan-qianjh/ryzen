package com.qianjh.ryzen.stream;

import com.qianjh.ryzen.entity.ImAccount;
import com.qianjh.ryzen.enums.ImAccountAssignStatus;
import com.qianjh.ryzen.framework.common.dto.Operator;
import com.qianjh.ryzen.framework.common.util.GsonUtils;
import com.qianjh.ryzen.framework.stream.consumer.StreamConsumer;
import com.qianjh.ryzen.model.SchedulerCommandResponse;
import com.qianjh.ryzen.service.ImAccountService;
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
    public Consumer<Message<String>> imsSchedulerCommandResponseSource() {
        return message -> {
            String payload = message.getPayload();

            log.info("receive message ::: {}", message);


            SchedulerCommandResponse response = GsonUtils.GSON.fromJson(payload, SchedulerCommandResponse.class);

            switch (response.type()) {
                case ASSIGN -> {
                    ImAccount imAccount = imAccountService.getByUk(response.imType(), response.accountCode());
                    if (imAccount == null) {
                        log.error("ImAccount不存在 ASSIGN ::: {}", payload);
                        return;
                    }
                    imAccountService.updateAssignStatus(imAccount, imAccount.getAssignStatus(), ImAccountAssignStatus.ASSIGNING, Operator.builder().build());
                    imAccountService.recordAssignment(imAccount, response.assignmentId());
                }
                case RELEASE -> {
                    ImAccount imAccount = imAccountService.getByUk(response.imType(), response.accountCode());
                    if (imAccount == null) {
                        log.error("ImAccount不存在 RELEASE ::: {}", payload);
                        return;
                    }
                    imAccountService.updateAssignStatus(imAccount, imAccount.getAssignStatus(), ImAccountAssignStatus.RELEASING, Operator.builder().build());
                    imAccountService.clearAssignment(imAccount);
                }
            }


        };
    }
}
