package com.qianjh.ryzen.service.dto;

import com.qianjh.ryzen.entity.MatchTagImAccount;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class MatchTagImAccountWithMatchTagView extends MatchTagImAccount {
    private String tag;
}