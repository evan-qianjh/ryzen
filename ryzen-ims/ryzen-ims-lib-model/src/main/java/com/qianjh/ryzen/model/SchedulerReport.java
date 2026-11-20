package com.qianjh.ryzen.model;

import com.qianjh.ryzen.enums.SchedulerReportType;
import lombok.Builder;

@Builder(toBuilder = true)
public record SchedulerReport(
        Long assignmentId,
        SchedulerReportType type
) {
}
