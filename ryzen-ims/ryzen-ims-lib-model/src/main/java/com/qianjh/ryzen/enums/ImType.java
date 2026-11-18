package com.qianjh.ryzen.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ImType {
    MEI_TUAN(
            "https://g.dianping.com/srimworkbench/home/index.html?appId=104&clientType=100900#/im/page/workbench/reception",
            ".shop-select-trigger__text"
    ),
    ;
    private final String platformUrl;
    private final String loginSuccessSelector;
}
