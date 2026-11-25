package com.qianjh.ryzen.service;

import com.qianjh.ryzen.ims.model.SchedulerCommandRequest;

/**
 *
 * @author QianJH
 */
public interface SchedulerCommandService {

    void handle(SchedulerCommandRequest command);
}
