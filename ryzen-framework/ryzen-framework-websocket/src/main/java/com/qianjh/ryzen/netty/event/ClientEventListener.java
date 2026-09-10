package com.qianjh.ryzen.netty.event;

/**
 *
 * @author QianJH
 */
public interface ClientEventListener<T extends ClientEvent>{
    void onEvent(T event);
}
