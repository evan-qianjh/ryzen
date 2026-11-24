package com.qianjh.ryzen.service.impl;

import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.qianjh.ryzen.entity.ReceptionAccount;
import com.qianjh.ryzen.mapper.ReceptionAccountMapper;
import com.qianjh.ryzen.service.ReceptionAccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ReceptionAccountServiceImpl extends ServiceImpl<ReceptionAccountMapper, ReceptionAccount> implements ReceptionAccountService {
}
