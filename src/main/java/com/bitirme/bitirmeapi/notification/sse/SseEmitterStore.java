package com.bitirme.bitirmeapi.notification.sse;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SseEmitterStore {
    public final static Map<Integer, SseEmitter> emitters = new ConcurrentHashMap<>();
}
