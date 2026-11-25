package com.qianjh.ryzen.ims.model;

import com.qianjh.ryzen.ims.enums.SchedulerReportType;
import lombok.Builder;

@Builder(toBuilder = true)
public record SchedulerReport(
        Long assignmentId,
        SchedulerReportType type
) {
}
