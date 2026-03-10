package com.calendar.mapper;

import com.calendar.dto.*;
import com.calendar.model.Event;
import com.calendar.model.Reminder;
import com.calendar.model.EventParticipant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface EventMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(target = "reminders", ignore = true)
    @Mapping(target = "participants", ignore = true)
    EventDTO toDTO(Event event);

    @Mapping(source = "user.id", target = "userId")
    EventDTO toDTOWithRelations(Event event);

    default List<ReminderDTO> mapReminders(List<Reminder> reminders) {
        if (reminders == null) return null;
        return reminders.stream()
            .map(r -> ReminderDTO.builder()
                .id(r.getId())
                .eventId(r.getEvent().getId())
                .reminderTime(r.getReminderTime())
                .status(r.getStatus())
                .message(r.getMessage())
                .sent(r.getSent())
                .sentAt(r.getSentAt())
                .notificationId(r.getNotificationId())
                .createdAt(r.getCreatedAt())
                .build())
            .collect(Collectors.toList());
    }

    default List<EventParticipantDTO> mapParticipants(List<EventParticipant> participants) {
        if (participants == null) return null;
        return participants.stream()
            .map(p -> EventParticipantDTO.builder()
                .id(p.getId())
                .eventId(p.getEvent().getId())
                .email(p.getEmail())
                .name(p.getName())
                .status(p.getStatus())
                .respondedAt(p.getRespondedAt())
                .createdAt(p.getCreatedAt())
                .build())
            .collect(Collectors.toList());
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "eventId", ignore = true)
    @Mapping(target = "sequence", ignore = true)
    @Mapping(target = "syncStatus", ignore = true)
    @Mapping(target = "lastSyncedAt", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "reminders", ignore = true)
    @Mapping(target = "participants", ignore = true)
    Event toEntity(CreateEventRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "eventId", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "reminders", ignore = true)
    @Mapping(target = "participants", ignore = true)
    void updateEntityFromDTO(UpdateEventRequest request, @MappingTarget Event event);

    List<EventDTO> toDTOList(List<Event> events);
}
