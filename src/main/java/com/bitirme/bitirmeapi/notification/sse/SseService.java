package com.bitirme.bitirmeapi.notification.sse;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@Service
public class SseService {

    public SseEmitter subscribeAndGetEmitter(int memberId) {
        final long ONE_HOUR = 3600000L;

        SseEmitter emitter = new SseEmitter(ONE_HOUR);
        sendInitEvent(emitter);

        SseEmitterStore.emitters.put(memberId, emitter);
        emitter.onCompletion(() -> SseEmitterStore.emitters.remove(memberId, emitter));
        emitter.onTimeout(() -> SseEmitterStore.emitters.remove(memberId, emitter));
        emitter.onError(e -> SseEmitterStore.emitters.remove(memberId, emitter));

        return emitter;
    }

    private void sendInitEvent(SseEmitter emitter) {
        try {
            emitter.send(SseEmitter.event().name("INIT"));
        } catch(IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Could not establish connection");
        }
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
