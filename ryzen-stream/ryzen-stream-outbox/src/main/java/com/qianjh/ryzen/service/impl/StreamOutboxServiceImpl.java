package com.qianjh.ryzen.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.qianjh.ryzen.framework.StreamOutbox;
import com.qianjh.ryzen.mapper.StreamOutboxMapper;
import com.qianjh.ryzen.service.ApplicationService;
import com.qianjh.ryzen.service.StreamOutboxService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class StreamOutboxServiceImpl extends ServiceImpl<StreamOutboxMapper, StreamOutbox> implements StreamOutboxService {

    private static final long RETRY_BASE_SECONDS = 30;
    private static final long RETRY_MAX_SECONDS = 30 * 60;

    private final ApplicationService applicationService;


    @Transactional(rollbackFor = Exception.class)
    @Override
    public List<StreamOutbox> claims() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime lockedTime = now.plusMinutes(-5);

        String instance = applicationService.getInstance();

        lambdaUpdate()
                .set(StreamOutbox::getLockedBy, instance)
                .set(StreamOutbox::getLockedTime, now)
                .and(w -> w
                        .isNull(StreamOutbox::getLockedBy)
                        .or()
                        .lt(StreamOutbox::getLockedTime, lockedTime)
                )
                .and(w -> w
                        .isNull(StreamOutbox::getNextRetryTime)
                        .or()
                        .lt(StreamOutbox::getNextRetryTime, now)
                )
                .and(w -> w
                        .isNull(StreamOutbox::getRetryCount)
                        .or()
                        .lt(StreamOutbox::getRetryCount, 20)
                )
                .orderByAsc(StreamOutbox::getId)
                .last("LIMIT " + 100)
                .update();

        return list(new LambdaQueryWrapper<StreamOutbox>()
                .eq(StreamOutbox::getLockedBy, instance)
                .orderByAsc(StreamOutbox::getId)
        );

    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void remove(StreamOutbox entity) {
        String instance = applicationService.getInstance();

        remove(new LambdaQueryWrapper<StreamOutbox>()
                .eq(StreamOutbox::getId, entity.getId())
                .eq(StreamOutbox::getLockedBy, instance)
        );
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void retry(StreamOutbox entity) {

        int retryCount = Optional.ofNullable(entity.getRetryCount()).orElse(0) + 1;
        long delay = calculateRetryDelay(retryCount);

        lambdaUpdate()
                .setSql("locked_by = NULL")
                .setSql("locked_time = NULL")
                .set(StreamOutbox::getRetryCount, retryCount)
                .setSql(retryCount >= 20, "next_retry_time = NULL")
                .set(retryCount < 20, StreamOutbox::getNextRetryTime, LocalDateTime.now().plusSeconds(delay))
                //
                .eq(StreamOutbox::getId, entity.getId())
                .eq(StreamOutbox::getLockedBy, applicationService.getInstance())
                .update();
    }

    /**
     * 公式：delay = random(0, min(30 × 2^retryCount, 30分钟))
     * 第1次失败最多等30秒，第2次最多60秒……达到30分钟后，仍然在 0~30分钟 内随机。
     *
     * @param retryCount 重试次数
     * @return 等待时常
     */
    private long calculateRetryDelay(int retryCount) {
        int exponent = Math.min(retryCount - 1, 6);

        long maxDelay = Math.min(
                RETRY_BASE_SECONDS * (1L << exponent),
                RETRY_MAX_SECONDS
        );

        return ThreadLocalRandom.current().nextLong(maxDelay + 1);
    }
}
