package com.qianjh.ryzen.framework.websocket.login;

/**
 *
 * @author QianJH
 */
public interface TokenParser {

    TokenParsed parse(String token);
}
