package com.qianjh.ryzen.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qianjh.ryzen.framework.StreamOutbox;
import org.apache.ibatis.annotations.Mapper;

/**
 *
 * @author QianJH
 */
@Mapper
public interface StreamOutboxMapper extends BaseMapper<StreamOutbox> {
}
