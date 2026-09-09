package com.qianjh.ryzen.util.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class TimePoints {
    private Long createTime;
    private Long updateTime;
    private List<TimePoint> points;

    public static TimePoints build() {
        long time = System.currentTimeMillis();
        return TimePoints.builder()
                .createTime(time)
                .updateTime(time)
                .points(new ArrayList<>())
                .build();
    }

    public void add(TimePoint point) {
        points.add(point);
        updateTime = System.currentTimeMillis();
    }
}
