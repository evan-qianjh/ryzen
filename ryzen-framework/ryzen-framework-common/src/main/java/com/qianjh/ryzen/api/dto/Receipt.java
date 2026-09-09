package com.qianjh.ryzen.api.dto;

import com.qianjh.ryzen.util.IdUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 回执
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class Receipt {

    @Schema(description = "回执ID")
    private String id;

    public static Receipt build(Long id) {
        return Receipt.builder().id(IdUtils.toString(id)).build();
    }
}
