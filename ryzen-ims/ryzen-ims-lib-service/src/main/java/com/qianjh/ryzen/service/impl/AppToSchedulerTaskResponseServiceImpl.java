package com.qianjh.ryzen.service.impl;

import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.qianjh.ryzen.entity.AppToSchedulerTaskResponse;
import com.qianjh.ryzen.mapper.AppToSchedulerTaskResponseMapper;
import com.qianjh.ryzen.service.ToSchedulerTaskResponseService;
import org.springframework.stereotype.Service;

@Service
public class AppToSchedulerTaskResponseServiceImpl extends ServiceImpl<AppToSchedulerTaskResponseMapper, AppToSchedulerTaskResponse> implements ToSchedulerTaskResponseService {

}
