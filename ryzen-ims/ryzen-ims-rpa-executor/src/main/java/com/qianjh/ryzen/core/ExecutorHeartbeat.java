package com.qianjh.ryzen.core;

import com.qianjh.ryzen.model.ExecutorSnapshot;
import com.qianjh.ryzen.transport.ExecutorTransport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 执行器心跳
 *
 * @author QianJH
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ExecutorHeartbeat {
    private final ExecutorWorker worker;
    private final ExecutorTransport transport;

    @Scheduled(fixedDelayString = "${ryzen.executor.heartbeat-interval-seconds:10}")
    public void heartbeat() {
        ExecutorSnapshot snapshot = worker.snapshot();
        transport.heartbeat(snapshot);

        log.debug("heartbeat ::: {}", snapshot);
    }
}
