package com.qianjh.ryzen.service.dto;

import com.qianjh.ryzen.entity.MatchTagServiceAccount;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class MatchTagServiceAccountWithMatchTagView extends MatchTagServiceAccount {
    private String tag;
}