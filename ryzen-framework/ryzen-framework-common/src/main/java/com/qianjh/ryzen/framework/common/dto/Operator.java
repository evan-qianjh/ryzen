package com.qianjh.ryzen.framework.common.dto;

import com.qianjh.ryzen.framework.common.dict.OperatorIdentity;
import lombok.Builder;

/**
 * 操作者
 *
 * @param identity 身份
 * @param id       ID
 * @param name     名称
 */
@Builder(toBuilder = true)
public record Operator(
        OperatorIdentity identity,
        Long id,
        String name
) {
}
