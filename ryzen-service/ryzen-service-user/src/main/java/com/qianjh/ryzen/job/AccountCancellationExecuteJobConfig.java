package com.qianjh.ryzen.job;

import org.quartz.JobDetail;
import org.quartz.SimpleTrigger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;

@Configuration
public class AccountCancellationExecuteJobConfig {

    @Bean
    public JobDetailFactoryBean accountCancellationExecuteJobDetail() {
        JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
        factoryBean.setJobClass(AccountCancellationExecuteJob.class);
        factoryBean.setDescription("账户注销-执行");
        factoryBean.setDurability(true);
        return factoryBean;
    }

    @Bean
    public SimpleTriggerFactoryBean accountCancellationExecuteJobTrigger(JobDetail accountCancellationExecuteJobDetail) {
        SimpleTriggerFactoryBean factoryBean = new SimpleTriggerFactoryBean();
        factoryBean.setJobDetail(accountCancellationExecuteJobDetail);
        factoryBean.setRepeatInterval(15_000);
        factoryBean.setRepeatCount(SimpleTrigger.REPEAT_INDEFINITELY);
        return factoryBean;
    }

}
