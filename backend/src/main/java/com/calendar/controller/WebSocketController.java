package com.calendar.controller;

import com.calendar.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
public class WebSocketController {

    @MessageMapping("/ping")
    public void handlePing(Principal principal, @Payload Map<String, Object> payload) {
        log.debug("Received ping from user: {}", principal.getName());
        // Simple ping/pong for connection health check
    }

    @MessageMapping("/sync/request")
    public void handleSyncRequest(Principal principal, @Payload Map<String, Object> payload) {
        log.info("Sync request from user: {}", principal.getName());
        // Trigger immediate sync for the user
    }
}
