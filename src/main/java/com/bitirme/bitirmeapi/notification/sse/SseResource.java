package com.bitirme.bitirmeapi.notification.sse;

import com.bitirme.bitirmeapi.security.MemberDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/api/sse")
public class SseResource {

    private final SseService sseService;

    @Autowired
    public SseResource(SseService sseService) {
        this.sseService = sseService;
    }

    @GetMapping(value = "/subscribe")
    public SseEmitter subscribe(@AuthenticationPrincipal MemberDetails principal,
                                HttpServletResponse response) {

        SseEmitter emitter = sseService.subscribeAndGetEmitter(principal.getId());
        response.addHeader("X-Accel-Buffering", "no");
        return emitter;
    }

}
