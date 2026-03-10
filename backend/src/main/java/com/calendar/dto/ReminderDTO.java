package com.calendar.dto;

import com.calendar.model.Reminder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReminderDTO {
    private Long id;
    private Long eventId;
    private LocalDateTime reminderTime;
    private Reminder.ReminderStatus status;
    private String message;
    private Boolean sent;
    private LocalDateTime sentAt;
    private String notificationId;
    private LocalDateTime createdAt;
}
