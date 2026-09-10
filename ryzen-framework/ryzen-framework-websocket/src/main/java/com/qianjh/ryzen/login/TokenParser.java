package com.qianjh.ryzen.login;

/**
 *
 * @author QianJH
 */
public interface TokenParser {

    TokenParsed parse(String token);
}
