package com.qianjh.ryzen.job;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.qianjh.ryzen.config.SchedulerProperties;
import com.qianjh.ryzen.entity.Executor;
import com.qianjh.ryzen.enums.ExecutorStatus;
import com.qianjh.ryzen.service.ExecutorService;
import com.qianjh.ryzen.service.ExecutorTaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.event.Level;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SchedulerMaintenanceJob {
    private final SchedulerProperties properties;
    private final ExecutorService executorService;
    private final ExecutorTaskService executorTaskService;

    @Scheduled(fixedDelayString = "${ryzen.scheduler.timeout-check-interval-seconds:5}")
    public void checkTimeouts() {
        LocalDateTime now = LocalDateTime.now();
        // 标记超时待处理任务
        boolean pending = executorTaskService.timeoutPendingTasks(now);
        // 标记未知进行中任务
        boolean uncertain = executorTaskService.unknownRunningTasks(now);
        if (pending || uncertain) {
            log.warn("Task timeout scan completed: pendingTimeout={}, executionUnknown={}", pending, uncertain);
        }

        // 计算出心跳超时基准时间
        LocalDateTime cutoff = now.plusSeconds(properties.getHeartbeatTimeoutSeconds() * -1);

        // 查询出心跳超时执行器
        List<Executor> executors = executorService.list(new LambdaQueryWrapper<Executor>()
                .ne(Executor::getStatus, ExecutorStatus.OFFLINE)
                .lt(Executor::getLastHeartbeatTime, cutoff)
        );

        // 依次下线执行器
        for (Executor executor : executors) {
            boolean success = executorService.offline(executor, cutoff);
            log.atLevel(success ? Level.INFO : Level.WARN)
                    .log("Executor marked OFFLINE: executorCode={}, accountCode={}",
                            executor.getId(), executor.getAccountCode()
                    );
        }
    }
}
