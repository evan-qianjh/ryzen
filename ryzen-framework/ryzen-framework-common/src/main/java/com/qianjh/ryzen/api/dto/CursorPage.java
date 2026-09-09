package com.qianjh.ryzen.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Schema(description = "游标分页")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class CursorPage<DTO> {

    @Schema(description = "记录")
    private List<DTO> records;

    @Schema(description = "还有更多")
    private Boolean hasNext;
}
