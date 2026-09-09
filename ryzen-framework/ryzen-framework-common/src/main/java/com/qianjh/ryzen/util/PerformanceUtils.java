package com.qianjh.ryzen.util;

import com.qianjh.ryzen.util.dto.TimePoint;
import com.qianjh.ryzen.util.dto.TimePoints;
import com.qianjh.ryzen.util.dto.TimePointsReport;

import java.util.ArrayList;
import java.util.List;

public final class PerformanceUtils {

    public static TimePointsReport report(TimePoints entity) {
        List<TimePoint> timePoints = entity.getPoints();

        List<TimePointsReport.Point> points = new ArrayList<>();

        // 整体耗时
        long usedTime = entity.getUpdateTime() - entity.getCreateTime();

        // 各自耗时
        Long lastPoint = null;
        for (TimePoint timePoint : timePoints) {
            if (lastPoint != null) {
                points.add(
                        TimePointsReport.Point.builder()
                                .label(timePoint.getLabel())
                                .usedTime(timePoint.getTimestamp() - lastPoint)
                                .build()
                );
            }
            lastPoint = timePoint.getTimestamp();
        }

        return TimePointsReport.builder()
                .usedTime(usedTime)
                .points(points)
                .build();
    }

}
