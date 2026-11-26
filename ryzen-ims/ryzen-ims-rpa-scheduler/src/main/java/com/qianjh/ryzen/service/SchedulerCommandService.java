package com.qianjh.ryzen.service;

import com.qianjh.ryzen.ims.stream.app_to_scheduler.command.type.AppToSchedulerCommandRequest;

/**
 *
 * @author QianJH
 */
public interface SchedulerCommandService {

    void handle(AppToSchedulerCommandRequest.Body body);
}
