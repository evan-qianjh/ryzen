package com.qianjh.ryzen.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qianjh.ryzen.entity.ExecutorTask;
import com.qianjh.ryzen.enums.ExecutorTaskStatus;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

@Repository
public interface ExecutorTaskMapper extends BaseMapper<ExecutorTask> {

    @Select(" SELECT * FROM " +
            "   ims_executor_task " +
            " WHERE " +
            "   account_code = #{accountCode} " +
            "   AND status = #{status}" +
            " ORDER BY created_time ASC, id ASC" +
            " LIMIT 1 " +
            " FOR UPDATE")
    ExecutorTask lock(@Param("accountCode") String accountCode, @Param("status") ExecutorTaskStatus status);
}
