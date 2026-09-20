package com.qianjh.ryzen.framework.websocket.netty.event;

/**
 *
 * @author QianJH
 */
public interface ClientEventListener<T extends ClientEvent>{
    void onEvent(T event);
}
