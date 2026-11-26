package com.qianjh.ryzen.service;

import com.qianjh.ryzen.ims.stream.domain.command.type.AppToSchedulerCommandRequest;

/**
 *
 * @author QianJH
 */
public interface SchedulerCommandService {

    void handle(AppToSchedulerCommandRequest.Body body);
}
