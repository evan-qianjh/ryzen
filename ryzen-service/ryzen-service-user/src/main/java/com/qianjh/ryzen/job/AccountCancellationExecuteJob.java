package com.qianjh.ryzen.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

/**
 * 账户注销-执行
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AccountCancellationExecuteJob implements Job {

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        // TODO
        log.info("AccountCancellationExecuteJob execute");
    }
}
