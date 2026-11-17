package com.qianjh.ryzen.service.impl;

import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.qianjh.ryzen.entity.Message;
import com.qianjh.ryzen.mapper.MessageMapper;
import com.qianjh.ryzen.service.MessageService;
import org.springframework.stereotype.Service;

@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements MessageService {
}
