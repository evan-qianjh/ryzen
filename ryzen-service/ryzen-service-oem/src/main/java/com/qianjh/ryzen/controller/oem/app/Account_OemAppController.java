package com.qianjh.ryzen.controller.oem.app;

import com.qianjh.ryzen.controller.oem.app.dto.GetAccountsResp;
import com.qianjh.ryzen.framework.common.dto.OffsetPage;
import com.qianjh.ryzen.framework.common.header.GatewayHeaderOem;
import com.qianjh.ryzen.framework.common.header.GatewayHeaderTenant;
import com.qianjh.ryzen.framework.http.model.Resp;
import com.qianjh.ryzen.framework.service.controller.oem._OemAppController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import static com.qianjh.ryzen.framework.service.controller.tenant._TenantAppController.PATH_PREFIX;

@Slf4j
@Tag(name = "账户")
@RestController
@RequestMapping(PATH_PREFIX)
@RequiredArgsConstructor
public class Account_OemAppController extends _OemAppController {


    @Operation(summary = "分页查询")
    @GetMapping("/accounts")
    public Resp<OffsetPage<GetAccountsResp>> page(@RequestHeader(GatewayHeaderOem.OEM_ID) Long oemId,
                                                  @RequestHeader(GatewayHeaderOem.ACCOUNT_ID) Long accountId,
                                                  //
                                                  @RequestParam(required = false) String username,
                                                  @RequestParam(required = false) Boolean enabled,
                                                  @RequestParam(required = false, defaultValue = DEFAULT_PAGE_INDEX) Integer pageIndex,
                                                  @RequestParam(required = false, defaultValue = DEFAULT_PAGE_SIZE) @Max(100) Integer pageSize) {
        // TODO

        return Resp.successOf(null);
    }

}
