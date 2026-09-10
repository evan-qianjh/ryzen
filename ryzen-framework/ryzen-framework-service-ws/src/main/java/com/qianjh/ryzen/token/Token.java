package com.qianjh.ryzen.token;

import com.google.gson.Gson;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author QianJH
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Schema(description = "token")
public class Token {
    @Schema(description = "账户ID", example = "1923970891776")
    private Long accountId;

    @Schema(description = "有效期（秒）", example = "1661651471")
    private Date exp;

    @Schema(description = "签名类型", allowableValues = "UP,AK", example = "AK")
    private String signType;

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
