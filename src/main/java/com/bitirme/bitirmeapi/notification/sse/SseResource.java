package com.bitirme.bitirmeapi.notification.sse;

import com.bitirme.bitirmeapi.security.MemberDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
@RequestMapping("/api/sse")
public class SseResource {

    private final SseService sseService;
    private static final Logger LOGGER = LoggerFactory.getLogger(SseResource.class);

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
