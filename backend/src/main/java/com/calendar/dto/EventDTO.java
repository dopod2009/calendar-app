package com.calendar.dto;

import com.calendar.model.Event;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventDTO {
    private Long id;
    private String eventId;
    private Long userId;
    private String title;
    private String description;
    private String location;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Boolean allDay;
    private Event.EventCategory category;
    private String color;
    private Boolean reminderEnabled;
    private Integer reminderMinutes;
    private Event.RecurrenceType recurrenceType;
    private String recurrenceRule;
    private Long sequence;
    private String source;
    private Event.SyncStatus syncStatus;
    private LocalDateTime lastSyncedAt;
    private String externalEventId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<ReminderDTO> reminders;
    private List<EventParticipantDTO> participants;
}
