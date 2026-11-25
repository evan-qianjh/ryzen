package com.qianjh.ryzen.service.impl;

import com.qianjh.ryzen.ims.model.ExecutorCommandResponse;
import com.qianjh.ryzen.service.ExecutorCommandService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class ExecutorCommandServiceImpl implements ExecutorCommandService {

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void handle(ExecutorCommandResponse response) {

    }
}
