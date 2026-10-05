package com.qianjh.ryzen.exception;

import com.qianjh.ryzen.framework.common.exception.BusinessException;
import com.qianjh.ryzen.framework.common.exception.TokenExpiredException;
import com.qianjh.ryzen.framework.http.model.Resp;
import io.netty.handler.codec.http.TooLongHttpLineException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.webflux.autoconfigure.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.webflux.error.DefaultErrorAttributes;
import org.springframework.boot.webflux.error.ErrorAttributes;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Mono;


@Slf4j
@Order(-2)
@Component
public class GlobalErrorWebExceptionHandler extends AbstractErrorWebExceptionHandler {

    public GlobalErrorWebExceptionHandler(DefaultErrorAttributes gea, ApplicationContext applicationContext,
                                          ServerCodecConfigurer serverCodecConfigurer) {
        super(gea, new WebProperties.Resources(), applicationContext);
        super.setMessageWriters(serverCodecConfigurer.getWriters());
        super.setMessageReaders(serverCodecConfigurer.getReaders());
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
    }

    private Mono<ServerResponse> renderErrorResponse(final ServerRequest request) {

        Throwable throwable = getError(request);

        // URI太大（
        if (throwable instanceof TooLongHttpLineException) {
            return ServerResponse.status(HttpStatus.URI_TOO_LONG).build();
        }

        // 下游服务不可用
        if (throwable instanceof NotFoundException exception) {
            log.error("NotFoundException :::  message={}, reason={}",
                    exception.getMessage(), exception.getReason(), exception);
            return ServerResponse.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }

        // token失效
        if (throwable instanceof TokenExpiredException) {
            return ServerResponse.status(HttpStatus.UNAUTHORIZED).build();
        }


        // 非法参数，spring Assert断言产生，用于校验用户提交的参数合法性
        if (throwable instanceof IllegalArgumentException) {
            return ServerResponse.status(HttpStatus.OK)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(Resp.failure(throwable.getMessage())));
        }

        // 业务异常
        if (throwable instanceof BusinessException exception) {
            return ServerResponse.status(HttpStatus.OK)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(Resp.failure(exception.getMc(), exception.getMa())));
        }

        // unknown
        log.error("Unknown Exception ::: message={}", throwable.getMessage(), throwable);
        return ServerResponse.status(HttpStatus.BAD_GATEWAY).build();
    }
}
