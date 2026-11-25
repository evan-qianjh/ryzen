package com.qianjh.ryzen.service;

import com.qianjh.ryzen.ims.model.ExecutorCommandResponse;

public interface ExecutorCommandService {
    void handle(ExecutorCommandResponse response);
}
