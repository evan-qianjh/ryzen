package com.qianjh.ryzen.controller.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class PostAccountTokenResp {

    @Schema(description = "ID")
    private String id;

    @Schema(description = "访问Token")
    private String accessToken;

    @Schema(description = "续签Token")
    private String refreshToken;
}
