package com.qianjh.ryzen.service.impl;

import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.qianjh.ryzen.entity.SchedulerTaskResponse;
import com.qianjh.ryzen.mapper.SchedulerTaskResponseMapper;
import com.qianjh.ryzen.service.SchedulerTaskResponseService;
import org.springframework.stereotype.Service;

@Service
public class AppSchedulerTaskResponseServiceImpl extends ServiceImpl<SchedulerTaskResponseMapper, SchedulerTaskResponse> implements SchedulerTaskResponseService {

}
