package com.qianjh.ryzen.netty.event;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 *
 * @author QianJH
 */
@Slf4j
public class ClientEventBus {

    private static final Map<Class<?>, List<ClientEventListener<?>>> LISTENERS = new ConcurrentHashMap<>();
    //    private static final ExecutorService executor = Executors.newFixedThreadPool(4);
    private static final ExecutorService executor =
            new ThreadPoolExecutor(
                    4,
                    8,
                    60L,
                    TimeUnit.SECONDS,
                    new LinkedBlockingQueue<>(1000),
                    new ThreadPoolExecutor.CallerRunsPolicy()
            );

    public static <T extends ClientEvent> void register(Class<T> eventType, ClientEventListener<T> listener) {
        LISTENERS
                .computeIfAbsent(eventType, k -> new CopyOnWriteArrayList<>())
                .add(listener);
    }

    @SuppressWarnings("unchecked")
    public static <T extends ClientEvent> void publish(T event) {
        List<ClientEventListener<?>> listeners = LISTENERS.get(event.getClass());
        if (listeners == null) {
            return;
        }

        for (ClientEventListener<?> listener : listeners) {
            executor.execute(() -> {
                try {
                    ((ClientEventListener<T>) listener).onEvent(event);
                } catch (Exception e) {
                    log.error("event error", e);
                }
            });
        }
    }
}
