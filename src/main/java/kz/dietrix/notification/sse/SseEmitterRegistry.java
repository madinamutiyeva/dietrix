package kz.dietrix.notification.sse;

import kz.dietrix.notification.dto.NotificationDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory registry of SSE emitters per userId.
 * Lightweight push: zero external services, works while a browser tab is open.
 */
@Slf4j
@Component
public class SseEmitterRegistry {

    /** 30 minutes — browsers will auto-reconnect via EventSource. */
    private static final long EMITTER_TIMEOUT = 30L * 60L * 1000L;

    private final Map<Long, List<SseEmitter>> emitters = new ConcurrentHashMap<>();

    public SseEmitter register(Long userId) {
        SseEmitter emitter = new SseEmitter(EMITTER_TIMEOUT);
        emitters.computeIfAbsent(userId, k -> new CopyOnWriteArrayList<>()).add(emitter);

        emitter.onCompletion(() -> remove(userId, emitter));
        emitter.onTimeout(()    -> remove(userId, emitter));
        emitter.onError(err     -> remove(userId, emitter));

        try {
            emitter.send(SseEmitter.event().name("connected").data("ok"));
        } catch (IOException e) {
            remove(userId, emitter);
        }
        log.debug("SSE registered for user {} (total emitters: {})", userId, count(userId));
        return emitter;
    }

    public void broadcast(Long userId, NotificationDto notification) {
        List<SseEmitter> list = emitters.get(userId);
        if (list == null || list.isEmpty()) return;
        for (SseEmitter em : list) {
            try {
                em.send(SseEmitter.event()
                        .name("notification")
                        .data(notification));
            } catch (IOException e) {
                remove(userId, em);
            }
        }
    }

    private void remove(Long userId, SseEmitter emitter) {
        List<SseEmitter> list = emitters.get(userId);
        if (list != null) {
            list.remove(emitter);
            if (list.isEmpty()) emitters.remove(userId);
        }
    }

    private int count(Long userId) {
        List<SseEmitter> list = emitters.get(userId);
        return list == null ? 0 : list.size();
    }
}

