package com.qianjh.ryzen.util.dto;

import com.qianjh.ryzen.util.AmountUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class Amount {
    private BigDecimal major;
    private long minor;
    private int scale;

    /**
     * 从展示单位转换
     *
     * @param major 展示单位
     * @param scale 精度
     * @return 金额
     */
    public static Amount ofMajor(BigDecimal major, int scale) {
        return AmountUtils.ofMajor(major, scale);
    }

    /**
     * 从货币单位转换
     *
     * @param minor 货币单位
     * @param scale 精度
     * @return 金额
     */
    public static Amount ofMinor(long minor, int scale) {
        return AmountUtils.ofMinor(minor, scale);
    }

    /**
     * 转换精度
     *
     * @param toScale 目标精度
     * @return 转换后结果
     */
    public Amount toScale(int toScale) {
        long minor = AmountUtils.toScale(this.minor, this.scale, toScale);
        return ofMinor(minor, toScale);
    }
}
