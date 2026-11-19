package com.qianjh.ryzen.controller.tenant.app;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.qianjh.ryzen.controller.tenant.app.dto.GetExecutorsResp;
import com.qianjh.ryzen.controller.tenant.app.dto.PostExecutorReq;
import com.qianjh.ryzen.controller.tenant.app.view.GetExecutorViews;
import com.qianjh.ryzen.entity.Executor;
import com.qianjh.ryzen.entity.ExecutorAssignment;
import com.qianjh.ryzen.enums.ExecutorStatus;
import com.qianjh.ryzen.enums.ImType;
import com.qianjh.ryzen.framework.common.dto.OffsetPage;
import com.qianjh.ryzen.framework.common.dto.Receipt;
import com.qianjh.ryzen.framework.common.header.GatewayHeaderTenant;
import com.qianjh.ryzen.framework.common.util.DateTimeUtils;
import com.qianjh.ryzen.framework.common.util.IdUtils;
import com.qianjh.ryzen.framework.http.controller.tenant._TenantAppController;
import com.qianjh.ryzen.framework.http.model.Resp;
import com.qianjh.ryzen.framework.http.webmvc.util.PageUtils;
import com.qianjh.ryzen.mapper.ExecutorMapper;
import com.qianjh.ryzen.service.ExecutorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@Slf4j
@Tag(name = "执行器")
@RestController
@RequestMapping(_TenantAppController.PATH_PREFIX)
@RequiredArgsConstructor
public class Executor_TenantAppController extends _TenantAppController {

    private final ExecutorMapper executorMapper;
    private final ExecutorService executorService;

    @Operation(summary = "分页查询")
    @GetMapping("/executors")
    public Resp<OffsetPage<GetExecutorsResp>> page(@RequestHeader(GatewayHeaderTenant.OEM_ID) Long oemId,
                                                   @RequestHeader(GatewayHeaderTenant.TENANT_ID) Long tenantId,
                                                   @RequestHeader(GatewayHeaderTenant.ACCOUNT_ID) Long accountId,
                                                   //
                                                   @RequestParam(required = false) ImType imType,
                                                   @RequestParam(required = false) String code,
                                                   @RequestParam(required = false) String accountCode,
                                                   @RequestParam(required = false) ExecutorStatus status,
                                                   //
                                                   @RequestParam(required = false, defaultValue = DEFAULT_PAGE_INDEX) Integer pageIndex,
                                                   @RequestParam(required = false, defaultValue = DEFAULT_PAGE_SIZE) Integer pageSize) {
        Page<GetExecutorViews> page = executorMapper.selectJoinPage(new Page<>(pageIndex, safePageSize(pageSize)),
                GetExecutorViews.class, new MPJLambdaWrapper<Executor>()
                        .selectAll(Executor.class)
                        .selectAs(ExecutorAssignment::getImType, GetExecutorViews::getImType)
                        .selectAs(ExecutorAssignment::getAccountCode, GetExecutorViews::getAccountCode)
                        //
                        .leftJoin(ExecutorAssignment.class, ExecutorAssignment::getId, Executor::getCurrentAssigmentId)
                        //
                        .eq(StringUtils.hasText(code), Executor::getCode, code)
                        .eq(status != null, Executor::getStatus, status)
                        .eq(imType != null, ExecutorAssignment::getImType, imType)
                        .eq(StringUtils.hasText(accountCode), ExecutorAssignment::getAccountCode, accountCode)
                        .orderByDesc(Executor::getId)
        );

        if (page.getRecords().isEmpty()) {
            return Resp.successOf(PageUtils.wrap(page, Collections.emptyList()));
        }

        List<GetExecutorsResp> records = page.getRecords().stream()
                .map(e -> GetExecutorsResp.builder()
                        .id(IdUtils.toString(e.getId()))
                        .code(e.getCode())
                        .imType(e.getImType())
                        .accountCode(e.getAccountCode())
                        .status(e.getStatus())
                        .currentOperation(e.getCurrentOperation())
                        .currentTaskId(e.getCurrentTaskId())
                        .registeredTime(DateTimeUtils.getTime(e.getRegisteredTime()))
                        .lastHeartbeatTime(DateTimeUtils.getTime(e.getLastHeartbeatTime()))
                        .createdTime(DateTimeUtils.getTime(e.getCreatedTime()))
                        .errorMessage(e.getErrorMessage())
                        .build()
                ).toList();

        OffsetPage<GetExecutorsResp> result = PageUtils.wrap(page, records);
        return Resp.successOf(result);
    }

    @Operation(summary = "创建")
    @PostMapping("/executor")
    public Resp<Receipt> create(@RequestHeader(GatewayHeaderTenant.OEM_ID) Long oemId,
                                @RequestHeader(GatewayHeaderTenant.TENANT_ID) Long tenantId,
                                @RequestHeader(GatewayHeaderTenant.ACCOUNT_ID) Long accountId,
                                @RequestBody @Validated PostExecutorReq body) {
        Executor entity = executorService.createIfAbsent(body.code());
        return Resp.successOf(Receipt.build(entity.getId()));
    }
}
