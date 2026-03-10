package com.calendar.dto;

import com.calendar.model.Event;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
public class CreateEventRequest {
    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title must be less than 200 characters")
    private String title;

    @Size(max = 1000, message = "Description must be less than 1000 characters")
    private String description;

    @Size(max = 50, message = "Location must be less than 50 characters")
    private String location;

    @NotNull(message = "Start time is required")
    @FutureOrPresent(message = "Start time must be in the future or present")
    private LocalDateTime startTime;

    @NotNull(message = "End time is required")
    private LocalDateTime endTime;

    private Boolean allDay;

    private Event.EventCategory category;

    @Size(max = 50, message = "Color must be less than 50 characters")
    private String color;

    private Boolean reminderEnabled;

    private Integer reminderMinutes;

    private Event.RecurrenceType recurrenceType;

    @Size(max = 100, message = "Recurrence rule must be less than 100 characters")
    private String recurrenceRule;

    @Size(max = 50, message = "Source must be less than 50 characters")
    private String source;

    private String externalEventId;

    private List<ReminderRequest> reminders;

    private List<ParticipantRequest> participants;
}
