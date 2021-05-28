package com.bitirme.bitirmeapi.notification;

import com.bitirme.bitirmeapi.notification.sse.SseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificationSender {

    private final SseService sseService;

    @Autowired
    public NotificationSender(SseService sseService) {
        this.sseService = sseService;
    }



}
