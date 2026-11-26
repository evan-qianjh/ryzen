package com.qianjh.ryzen.service;

import com.qianjh.ryzen.ims.dto.ExecutorCommandResponse;

public interface ExecutorCommandService {
    void handle(ExecutorCommandResponse response);
}
