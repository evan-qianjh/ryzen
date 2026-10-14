package com.qianjh.ryzen.plugin.sfexpress.service;

import com.baomidou.mybatisplus.spring.service.IService;
import com.qianjh.ryzen.plugin.sfexpress.entity.SFExpress;

/**
 * @author QianJH
 */
public interface SFExpressService extends IService<SFExpress> {

    SFExpress mustGet(Long oemId);
}
