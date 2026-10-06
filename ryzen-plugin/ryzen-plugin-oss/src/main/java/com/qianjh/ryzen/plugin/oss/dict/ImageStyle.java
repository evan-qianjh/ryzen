package com.qianjh.ryzen.plugin.oss.dict;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author QianJH
 */
@Getter
@AllArgsConstructor
public enum ImageStyle {
    PREVIEW_DEFAULT("preview-default"),

    PREVIEW_AVATAR_ORIGINAL_FORMAT("preview-avatar-original-format"),

    PREVIEW_AVATAR_PNG("preview-avatar-png");
    private final String code;
}
