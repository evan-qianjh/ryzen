package com.qianjh.ryzen.util.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class TimePoint {
    private String label;
    private long timestamp;

    public static TimePoint build(String label) {
        return TimePoint.builder().label(label).timestamp(System.currentTimeMillis()).build();
    }
}
