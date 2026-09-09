package com.qianjh.ryzen.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Schema(description = "下标分页")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class OffsetPage<DTO> {

    @Schema(description = "记录")
    private List<DTO> records;

    @Schema(description = "当前页")
    private Long current;

    @Schema(description = "每页显示的条数")
    private Long size;

    @Schema(description = "总记录数")
    private Long total;

    @Schema(description = "总页数")
    private Long pages;
}
