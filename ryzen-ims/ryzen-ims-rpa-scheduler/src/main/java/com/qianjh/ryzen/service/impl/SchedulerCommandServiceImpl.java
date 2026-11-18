package com.qianjh.ryzen.service.impl;

import com.qianjh.ryzen.model.ExecutorCommand;
import com.qianjh.ryzen.model.SchedulerCommand;
import com.qianjh.ryzen.service.SchedulerCommandService;
import com.qianjh.ryzen.transport.SchedulerKafkaTransport;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 *
 * @author QianJH
 */
@Service
@RequiredArgsConstructor
public class SchedulerCommandServiceImpl implements SchedulerCommandService {

    private final SchedulerKafkaTransport transport;

    @Override
    public void handle(SchedulerCommand command) {
        // TODO 查出最近分配过且IDLE的executor，如果没有则获取最早注册IDLE

        // TODO 创建assignment

        ExecutorCommand executorCommand = ExecutorCommand.builder()
                // TODO
                .build();
        try {
            transport.executorCommand(executorCommand).get(10, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new RuntimeException(e);
        }
    }
}
