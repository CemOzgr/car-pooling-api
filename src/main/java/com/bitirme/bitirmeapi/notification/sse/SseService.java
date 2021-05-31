package com.bitirme.bitirmeapi.notification.sse;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class SseService {

    private final ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(1);

    public SseEmitter subscribeAndGetEmitter(int memberId) {
        final long ONE_HOUR = 3600000L;

        SseEmitter emitter = new SseEmitter(ONE_HOUR);
        sendNamedEvent(emitter, "INIT");

        SseEmitterStore.emitters.put(memberId, emitter);
        emitter.onCompletion(() -> SseEmitterStore.emitters.remove(memberId, emitter));
        emitter.onTimeout(() -> SseEmitterStore.emitters.remove(memberId, emitter));
        emitter.onError(e -> SseEmitterStore.emitters.remove(memberId, emitter));

        sendPingToAllEmitters();

        return emitter;
    }

    private void sendNamedEvent(SseEmitter emitter, String name) {
        try {
            emitter.send(SseEmitter.event().name(name));
        } catch(IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Could not establish connection");
        }
    }

    public void sendPingToAllEmitters() {
        scheduledThreadPool.scheduleAtFixedRate(() ->
                SseEmitterStore.emitters.values()
                        .forEach(emitter -> sendNamedEvent(emitter, "PING")), 149, 149, TimeUnit.SECONDS);
    }

    public void sendNamedEventToMember(int memberId, Object eventObject, String eventName) {
        SseEmitter emitter = SseEmitterStore.emitters.get(memberId);
        if(emitter != null) {
            try {
                emitter.send(SseEmitter.event().name(eventName).data(eventObject));
            } catch (IOException e) {
                SseEmitterStore.emitters.remove(memberId, emitter);
            }
        }
    }

}
