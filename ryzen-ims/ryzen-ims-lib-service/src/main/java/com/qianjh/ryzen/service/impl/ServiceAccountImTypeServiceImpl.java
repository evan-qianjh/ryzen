package com.qianjh.ryzen.service.impl;

import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.qianjh.ryzen.entity.ServiceAccountImType;
import com.qianjh.ryzen.mapper.ServiceAccountImTypeMapper;
import com.qianjh.ryzen.service.ServiceAccountImTypeService;
import org.springframework.stereotype.Service;

@Service
public class ServiceAccountImTypeServiceImpl extends ServiceImpl<ServiceAccountImTypeMapper, ServiceAccountImType> implements ServiceAccountImTypeService {
}
