package com.qianjh.ryzen.exception;

import com.qianjh.ryzen.api.Resp;
import com.qianjh.ryzen.api.RespMc;
import com.qianjh.ryzen.util.McUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Enumeration;
import java.util.List;

/**
 * @author QianJH
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Resp<?>> business(BusinessException ex) {
//        log.error(ex.getMessage(), ex);
        Resp<?> resp = Resp.failure(ex.getMc(), ex.getMa());
        return new ResponseEntity<>(resp, HttpStatus.OK);
    }

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<Resp<?>> tokenExpired(TokenExpiredException ex) {
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Resp<?>> illegalArgumentException(IllegalArgumentException ex) {
        log.error(ex.getMessage(), ex);
        Resp<?> resp = Resp.failure(ex.getMessage(), null);
        return new ResponseEntity<>(resp, HttpStatus.OK);
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<Resp<?>> missingRequestHeaderException(MissingRequestHeaderException ex, HttpServletRequest request) {
        log.error(ex.getMessage(), ex);

        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            String value = request.getHeader(name);
            log.error("key={}, value={}", name, value);
        }

        Resp<?> resp = Resp.failure(McUtils.i18n(RespMc.MISSING_HEADER));
        return new ResponseEntity<>(resp, HttpStatus.OK);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Resp<?>> missingServletRequestParameterException(MissingServletRequestParameterException ex, HttpServletRequest request) {
        Resp<?> resp = Resp.failure(McUtils.i18n(RespMc.MISSING_ARGUMENT));
        return new ResponseEntity<>(resp, HttpStatus.OK);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Resp<?>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
        String message = fieldErrors.stream()
                .map(entity -> String.format("%s%s", entity.getField(), entity.getDefaultMessage()))
                .findFirst().orElse(null);

        Resp<?> resp = Resp.failure(message, null);
        return new ResponseEntity<>(resp, HttpStatus.OK);
    }

    @ExceptionHandler(UnsupportedOperationException.class)
    public ResponseEntity<Resp<?>> unsupportedOperationException(UnsupportedOperationException ex) {
        log.error(ex.getMessage(), ex);
        Resp<?> resp = Resp.failure(ex.getMessage(), null);
        return new ResponseEntity<>(resp, HttpStatus.OK);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Resp<?>> noResourceFoundException(NoResourceFoundException ex) {
        log.error(ex.getMessage(), ex);
        Resp<?> resp = Resp.failure(ex.getMessage(), null);
        return new ResponseEntity<>(resp, HttpStatus.OK);
    }


    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Resp<?>> httpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
        log.error(ex.getMessage(), ex);
        Resp<?> resp = Resp.failure();
        return new ResponseEntity<>(resp, HttpStatus.NOT_FOUND);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<Resp<?>> exception(Exception ex) {
        log.error("exception", ex);
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
