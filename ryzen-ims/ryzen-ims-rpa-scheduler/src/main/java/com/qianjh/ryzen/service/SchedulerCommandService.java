package com.qianjh.ryzen.service;

import com.qianjh.ryzen.model.SchedulerCommand;

/**
 *
 * @author QianJH
 */
public interface SchedulerCommandService {

    void handle(SchedulerCommand command);
}
