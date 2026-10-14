package com.qianjh.ryzen.plugin.sfexpress.express.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SearchType {
    NORMAL(1),
    RETURN(2),;
    private final int number;
}
