package com.qianjh.ryzen.stream.consumer;

import com.qianjh.ryzen.stream.model.StreamHeader;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;

/**
 * @author QianJH
 */
public interface StreamConsumer {

    default Logger log() {
        return LoggerFactory.getLogger(this.getClass());
    }

    default void domainUnknown(String domain, Message<?> message) {
        log().warn("未处理的事件 ::: domain={}, message={}", domain, message);
    }

    default void domainIgnore(String domain, Message<?> message) {
        log().debug("忽略事件 ::: domain={}, message={}", domain, message);
    }

    default void typeUnknown(String type, String payload) {
        log().warn("未处理的事件 ::: type={}, payload={}", type, payload);
    }

    default void typeIgnore(String type, String payload) {
        log().debug("忽略事件 ::: type={}, payload={}", type, payload);
    }

    default String getStringHeader(Object object) {
        if (object == null) {
            return null;
        }
        return String.valueOf(object);
    }

    default Long getLongHeader(Object object) {
        if (object == null) {
            return null;
        }
        String string = getStringHeader(object);
        if (StringUtils.isBlank(string)) {
            return null;
        }
        return Long.parseLong(string);
    }

    default String getDomain(MessageHeaders headers) {
        return getStringHeader(headers.get(StreamHeader.DOMAIN));
    }

    default String getType(MessageHeaders headers) {
        return getStringHeader(headers.get(StreamHeader.TYPE));
    }

    default Long getTenant(MessageHeaders headers) {
        return getLongHeader(headers.get(StreamHeader.TENANT));
    }

    default Long getTime(MessageHeaders headers) {
        return getLongHeader(headers.get(StreamHeader.TIME));
    }
}
