package com.qianjh.ryzen.plugin.sfexpress.service;

import com.baomidou.mybatisplus.spring.service.IService;
import com.qianjh.ryzen.plugin.sfexpress.entity.PluginSFExpress;

/**
 * @author QianJH
 */
public interface PluginSFExpressService extends IService<PluginSFExpress> {

    PluginSFExpress mustGet(Long oemId);
}
