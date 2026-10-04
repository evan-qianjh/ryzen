package com.qianjh.ryzen.controller.saas.app;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qianjh.ryzen.controller.saas.app.dto.GetAccountsResp;
import com.qianjh.ryzen.entity.Account;
import com.qianjh.ryzen.framework.common.dto.OffsetPage;
import com.qianjh.ryzen.framework.common.header.GatewayHeaderSaas;
import com.qianjh.ryzen.framework.common.util.DateTimeUtils;
import com.qianjh.ryzen.framework.common.util.IdUtils;
import com.qianjh.ryzen.framework.http.model.Resp;
import com.qianjh.ryzen.framework.service.controller.saas._SaasAppController;
import com.qianjh.ryzen.framework.service.util.PageUtils;
import com.qianjh.ryzen.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.qianjh.ryzen.framework.service.controller.saas._SaasAppController.PATH_PREFIX;

@Slf4j
@Tag(name = "账户")
@RestController
@RequestMapping(PATH_PREFIX)
@RequiredArgsConstructor
public class Account_SaasAppController extends _SaasAppController {

    private final AccountService accountService;


    @Operation(summary = "分页查询")
    @GetMapping("/accounts")
    public Resp<OffsetPage<GetAccountsResp>> page(
            @RequestHeader(GatewayHeaderSaas.ACCOUNT_ID) Long accountId,
            //
            @RequestParam(required = false) String username,
            @RequestParam(required = false) Boolean enabled,
            @RequestParam(required = false, defaultValue = DEFAULT_PAGE_INDEX) Integer pageIndex,
            @RequestParam(required = false, defaultValue = DEFAULT_PAGE_SIZE) @Max(100) Integer pageSize) {
        Page<Account> page = accountService.page(new Page<>(pageIndex, pageSize), new LambdaQueryWrapper<Account>()
                .eq(enabled != null, Account::getEnabled, enabled)
                .like(StringUtils.isNotBlank(username), Account::getUsername, username)
                .orderByDesc(Account::getId)
        );

        List<Account> _records = page.getRecords();

        List<GetAccountsResp> records = _records.stream()
                .map(e -> GetAccountsResp.builder()
                        .id(IdUtils.toString(e.getId()))
                        .nickname(e.getNickname())
                        .username(e.getUsername())
                        .email(e.getEmail())
                        .enabled(e.getEnabled())
                        .administrator(e.isAdministrator())
                        .createdTime(DateTimeUtils.getTime(e.getCreatedTime()))
                        .build()
                ).toList();

        return Resp.successOf(PageUtils.wrap(page, records));
    }

}
