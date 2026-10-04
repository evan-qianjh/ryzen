package com.qianjh.ryzen.service.impl;

import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.qianjh.ryzen.entity.Oem;
import com.qianjh.ryzen.mapper.OemMapper;
import com.qianjh.ryzen.service.OemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class OemServiceImpl extends ServiceImpl<OemMapper, Oem> implements OemService {
}
