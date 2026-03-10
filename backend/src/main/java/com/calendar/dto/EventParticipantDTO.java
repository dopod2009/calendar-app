package com.calendar.dto;

import com.calendar.model.EventParticipant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventParticipantDTO {
    private Long id;
    private Long eventId;
    private String email;
    private String name;
    private EventParticipant.ParticipantStatus status;
    private LocalDateTime respondedAt;
    private LocalDateTime createdAt;
}
