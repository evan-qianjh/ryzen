package com.qianjh.ryzen.stream.subscriber;

import com.qianjh.ryzen.framework.common.util.GsonUtils;
import com.qianjh.ryzen.ims.dto.ExecutorSnapshot;
import com.qianjh.ryzen.ims.stream.executor_to_scheduler.report.ExecutorToSchedulerReportHeartbeat;
import com.qianjh.ryzen.ims.stream.executor_to_scheduler.report.ExecutorToSchedulerReportRegister;
import com.qianjh.ryzen.service.ExecutorRegistryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * @author QianJH
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ExecutorToSchedulerReportHeartbeatSubscriberImpl implements ExecutorToSchedulerReportHeartbeat.Subscriber {

    private final ExecutorRegistryService executorRegistryService;

    @Override
    public void subscribe(ExecutorToSchedulerReportHeartbeat.Body body) {
        log.debug("消费 ExecutorToSchedulerReportHeartbeat ::: {}", body);

        // 搭配 topic的message.timestamp.type=LogAppendTime，获取的是消息发送到broker的时间，避免executor与scheduler时间不一致
//        long timestamp = record.timestamp(); // TODO kafak时间
        long timestamp = System.currentTimeMillis();
        // 丢弃1分钟前的消息
        long expireTime = System.currentTimeMillis() - 60 * 1000L;
        if (timestamp < expireTime) {
            log.error("忽略过期消息 ::: timestamp={}, payload={}", timestamp, body);
            return;
        }

//        handle("executor heartbeat", payload, ExecutorSnapshot.class, executorRegistryService::heartbeat);
        executorRegistryService.heartbeat(body, timestamp);
    }
}
