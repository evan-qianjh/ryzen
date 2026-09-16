package com.qianjh.ryzen.dict;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author QianJH
 */
@Getter
@AllArgsConstructor
public enum VideoStyle {
    PREVIEW_DEFAULT("snapshot,t_1000,ar_auto,f_jpg");
    private final String code;
}
