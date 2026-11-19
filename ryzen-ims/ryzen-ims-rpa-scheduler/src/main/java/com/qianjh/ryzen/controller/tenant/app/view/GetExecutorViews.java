package com.qianjh.ryzen.controller.tenant.app.view;

import com.qianjh.ryzen.entity.Executor;
import com.qianjh.ryzen.enums.ImType;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class GetExecutorViews extends Executor {
    private ImType imType;
    private String accountCode;
}
