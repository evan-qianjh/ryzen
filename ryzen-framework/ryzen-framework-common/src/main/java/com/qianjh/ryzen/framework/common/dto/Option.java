package com.qianjh.ryzen.framework.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 选项
 *
 * @author QianJH
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class Option {
    @Schema(description = "标签名")
    private String label;

    @Schema(description = "标签值")
    private String value;

    @Schema(description = "分组")
    private String group;

    @Schema(description = "图标")
    private String icon;
}
