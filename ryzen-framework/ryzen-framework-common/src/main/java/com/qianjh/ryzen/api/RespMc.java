package com.qianjh.ryzen.api;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 *
 * @author QianJH
 */
@Getter
@AllArgsConstructor
public enum RespMc implements Mc {

    SUCCESS("Success", "成功"),

    FAILURE("failure", "失败"),

    SYSTEM_EXCEPTION("System Exception", "系统异常"),

    UNAVAILABLE("Unavailable", "不可用"),

    TIMEOUT("Timeout", "请求超时"),

    ACCESS_DENIED("Access denied", "禁止访问"),

    ACCOUNT_UNAVAILABLE("Account unavailable", "账户不可用"),

    SYSTEM_UPGRADE("System upgrade", "系统升级中，请稍后"),

    DUPLICATE_SUBMIT("Duplicate submission", "请勿重复提交"),

    DISABLED_FUNCTION("Disabled Function", "禁用的功能"),

    LOGIN_FAIL("Account or password incorrect", "账号或密码错误"),

    /* *************************  ************************* */

    TARGET_NOT_EXIST("Target not exist", "目标不存在"),
    TARGET_ALREADY_EXIST("Target already exist", "目标已存在"),
    TARGET_CONFLICT("Target conflict", "目标冲突"),

    /* *************************  ************************* */

    PENDING_APPROVAL("Pending approval", "已提交待审核"),

    /* *************************  ************************* */

    ILLEGAL_ACCESS("Illegal access", "没有权限"),
    ILLEGAL_ARGUMENT("Illegal argument", "非法参数"),
    ILLEGAL_CONTENT("Illegal content", "非法内容"),

    /* *************************  ************************* */

    UNSUPPORTED_OPERATION("Unsupported operation", "不支持的操作"),
    UNSUPPORTED_SAME_IDENTIFY_OPERATION("Unsupported same account-identify operation", "不支持同实名间操作"),
    UNSUPPORTED_MINOR("Minors are not supported", "不支持未成年人"),

    /* *************************  ************************* */

    MISSING_ARGUMENT("Missing argument", "缺少参数"),
    MISSING_HEADER("Missing header", "缺少请求头"),

    /* *************************  ************************* */

    ALREADY_CLOSED("Already Closed", "已关闭"),
    ALREADY_EXPIRED("Already Closed", "已过期"),

    /* *************************  ************************* */

    BAD_CIPHERTEXT("Bad Ciphertext", "无效的密文"),
    BAD_PASSWORD("Incorrect password", "密码错误"),

    /* *************************  ************************* */

    EXIST_UNCLOSED("Some are not closed", "存在未关闭的记录"),

    /* *************************  ************************* */

    NO_ACCOUNT_IDENTITY("Please account identity", "未实名认证"),
    ASSET_NOT_EXIST("Asset not exist", "资产不存在"),
    INSUFFICIENT_AVAILABLE_ASSETS("Insufficient available assets", "可用资产不足"),
    PARAMETER_IS_OUTDATED("Parameter is outdated", "请刷新页面"),
    OTHERS_ARE_PURCHASING("Others are purchasing", "别人购买中"),
    OFF_SHELF("Not for sale", "已下架"),

    ;

    private final String en;
    private final String zh_cn;
}
