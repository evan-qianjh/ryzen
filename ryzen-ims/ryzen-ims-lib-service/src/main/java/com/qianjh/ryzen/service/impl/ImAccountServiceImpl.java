package com.qianjh.ryzen.service.impl;

import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.qianjh.ryzen.entity.ImAccount;
import com.qianjh.ryzen.mapper.ImAccountMapper;
import com.qianjh.ryzen.service.ImAccountService;
import org.springframework.stereotype.Service;

@Service
public class ImAccountServiceImpl extends ServiceImpl<ImAccountMapper, ImAccount> implements ImAccountService {
}
