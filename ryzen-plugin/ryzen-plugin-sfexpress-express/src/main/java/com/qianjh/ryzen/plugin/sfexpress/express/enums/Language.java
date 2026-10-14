package com.qianjh.ryzen.plugin.sfexpress.express.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Language {
    ZH_CN("zh-CN"),
    ZH_TW("zh-TW"),
    ZH_HK("zh-HK"),
    ZH_MO("zh-MO"),
    EN("en"),
    ;
    private final String code;
}
