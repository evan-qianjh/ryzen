package com.qianjh.ryzen.api;

import com.qianjh.ryzen.util.McUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

/**
 *
 * @author QianJH
 */
@Data
@NoArgsConstructor
public class Resp<T> {
    private final static Integer RC_SUCCESS = 0;
    private final static Integer RC_FAILURE = 1;

//    private static Resp<Object> SUCCESS = new Resp<>(RC_SUCCESS, RespMc.SUCCESS.name(), Collections.emptyList(), null);
//    private static Resp<Object> FAILURE = new Resp<>(RC_FAILURE, RespMc.FAILURE.name(), Collections.emptyList(), null);

    @Schema(description = "Return Code, 0=成功;1=失败")
    private Integer rc;

    @Schema(description = "Message Code, 国际化编码，客户端国际化使用")
    private String mc;

    @Schema(description = "Message Args, 国际化参数，客户端国际化使用")
    private List<Object> ma;

    @Schema(description = "返回数据")
    private T result;

    private Resp(Integer rc, String mc, List<Object> ma, T result) {
        this.rc = rc;
        this.mc = mc;
        this.ma = ma;
        this.result = result;
    }

    public static boolean isSuccess(Resp<?> resp) {
        return RC_SUCCESS.equals(resp.getRc());
    }

    /* SUCCESS ----------------------------------------------- */

    public static Resp<Object> success() {
        return new Resp<>(RC_SUCCESS, McUtils.i18n(RespMc.SUCCESS), Collections.emptyList(), null);
    }

    public static <T> Resp<T> success(String mc) {
        return new Resp<>(RC_SUCCESS, mc, Collections.emptyList(), null);
    }

    public static <T> Resp<T> success(String mc, List<Object> ma) {
        return new Resp<>(RC_SUCCESS, mc, ma, null);
    }

    public static <T> Resp<T> success(String mc, List<Object> ma, T result) {
        return new Resp<>(RC_SUCCESS, mc, ma, result);
    }

    public static <T> Resp<T> successOf(T result) {
        return new Resp<>(RC_SUCCESS, McUtils.i18n(RespMc.SUCCESS), Collections.emptyList(), result);
    }


    /* FAILURE ----------------------------------------------- */

    public static Resp<Object> failure() {
        return new Resp<>(RC_FAILURE, McUtils.i18n(RespMc.FAILURE), Collections.emptyList(), null);
    }

    public static <T> Resp<T> failure(String mc) {
        return new Resp<>(RC_FAILURE, mc, Collections.emptyList(), null);
    }

    public static <T> Resp<T> failure(String mc, List<Object> ma) {
        return new Resp<>(RC_FAILURE, mc, ma, null);
    }

    public static <T> Resp<T> failureOf(T result) {
        return new Resp<>(RC_FAILURE, McUtils.i18n(RespMc.FAILURE), Collections.emptyList(), result);
    }

    /* Custom ----------------------------------------------- */

    public static <T> Resp<T> custom(Integer rc, String mc, List<Object> ma, T result) {
        return new Resp<>(rc, mc, ma, result);
    }

}
