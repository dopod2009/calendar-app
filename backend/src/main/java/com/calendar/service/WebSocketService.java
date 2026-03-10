package com.calendar.service;

import com.calendar.dto.EventDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebSocketService {

    private final SimpMessagingTemplate messagingTemplate;

    public void notifyEventCreated(Long userId, EventDTO event) {
        log.info("Notifying event created: {} to user: {}", event.getId(), userId);
        messagingTemplate.convertAndSendToUser(
            userId.toString(),
            "/queue/events",
            EventNotification.created(event)
        );
    }

    public void notifyEventUpdated(Long userId, EventDTO event) {
        log.info("Notifying event updated: {} to user: {}", event.getId(), userId);
        messagingTemplate.convertAndSendToUser(
            userId.toString(),
            "/queue/events",
            EventNotification.updated(event)
        );
    }

    public void notifyEventDeleted(Long userId, Long eventId) {
        log.info("Notifying event deleted: {} to user: {}", eventId, userId);
        messagingTemplate.convertAndSendToUser(
            userId.toString(),
            "/queue/events",
            EventNotification.deleted(eventId)
        );
    }

    public void notifySyncRequired(Long userId, String message) {
        log.info("Notifying sync required for user: {}", userId);
        messagingTemplate.convertAndSendToUser(
            userId.toString(),
            "/queue/sync",
            SyncNotification.required(message)
        );
    }

    public void notifyReminder(Long userId, EventDTO event, String message) {
        log.info("Sending reminder notification to user: {} for event: {}", userId, event.getId());
        messagingTemplate.convertAndSendToUser(
            userId.toString(),
            "/queue/reminders",
            ReminderNotification.of(event, message)
        );
    }

    // Notification DTOs
    @lombok.Data
    @lombok.AllArgsConstructor
    @lombok.NoArgsConstructor
    public static class EventNotification {
        private String type;
        private EventDTO event;
        private Long eventId;
        private Long timestamp;

        public static EventNotification created(EventDTO event) {
            return new EventNotification("CREATED", event, null, System.currentTimeMillis());
        }

        public static EventNotification updated(EventDTO event) {
            return new EventNotification("UPDATED", event, null, System.currentTimeMillis());
        }

        public static EventNotification deleted(Long eventId) {
            return new EventNotification("DELETED", null, eventId, System.currentTimeMillis());
        }
    }

    @lombok.Data
    @lombok.AllArgsConstructor
    @lombok.NoArgsConstructor
    public static class SyncNotification {
        private String type;
        private String message;
        private Long timestamp;

        public static SyncNotification required(String message) {
            return new SyncNotification("SYNC_REQUIRED", message, System.currentTimeMillis());
        }
    }

    @lombok.Data
    @lombok.AllArgsConstructor
    @lombok.NoArgsConstructor
    public static class ReminderNotification {
        private EventDTO event;
        private String message;
        private Long timestamp;

        public static ReminderNotification of(EventDTO event, String message) {
            return new ReminderNotification(event, message, System.currentTimeMillis());
        }
    }
}
