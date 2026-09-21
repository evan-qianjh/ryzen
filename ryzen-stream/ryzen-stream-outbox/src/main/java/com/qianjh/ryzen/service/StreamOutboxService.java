package com.qianjh.ryzen.service;

import com.baomidou.mybatisplus.spring.service.IService;
import com.qianjh.ryzen.framework.StreamOutbox;

import java.util.List;

/**
 * @author QianJH
 */
public interface StreamOutboxService extends IService<StreamOutbox> {

    /**
     * 提取
     *
     * @return 实体
     */
    List<StreamOutbox> claims();

    /**
     * 移除
     *
     * @param entity 实体
     */
    void remove(StreamOutbox entity);

    /**
     * 重试
     *
     * @param entity 实体
     */
    void retry(StreamOutbox entity);
}
