package com.calendar.service;

import com.calendar.dto.*;
import com.calendar.mapper.EventMapper;
import com.calendar.model.Event;
import com.calendar.model.Reminder;
import com.calendar.model.EventParticipant;
import com.calendar.model.User;
import com.calendar.repository.EventRepository;
import com.calendar.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final EventMapper eventMapper;

    @Transactional
    public EventDTO createEvent(Long userId, CreateEventRequest request) {
        log.info("Creating event for user: {}", userId);

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        Event event = eventMapper.toEntity(request);
        event.setUser(user);
        event.setEventId(UUID.randomUUID().toString());
        event.setSequence(1L);
        event.setSyncStatus(Event.SyncStatus.PENDING);
        event.setDeleted(false);

        if (request.getReminders() != null) {
            List<Reminder> reminders = request.getReminders().stream()
                .map(r -> Reminder.builder()
                    .event(event)
                    .reminderTime(r.getReminderTime())
                    .message(r.getMessage())
                    .status(Reminder.ReminderStatus.PENDING)
                    .sent(false)
                    .build())
                .collect(Collectors.toList());
            event.setReminders(reminders);
        }

        if (request.getParticipants() != null) {
            List<EventParticipant> participants = request.getParticipants().stream()
                .map(p -> EventParticipant.builder()
                    .event(event)
                    .email(p.getEmail())
                    .name(p.getName())
                    .status(EventParticipant.ParticipantStatus.PENDING)
                    .build())
                .collect(Collectors.toList());
            event.setParticipants(participants);
        }

        event = eventRepository.save(event);
        return eventMapper.toDTO(event);
    }

    @Transactional
    public EventDTO updateEvent(Long userId, Long eventId, UpdateEventRequest request) {
        log.info("Updating event: {} for user: {}", eventId, userId);

        Event event = eventRepository.findByIdAndUserIdAndDeletedFalse(eventId, userId)
            .orElseThrow(() -> new RuntimeException("Event not found"));

        eventMapper.updateEntityFromDTO(request, event);
        event.setSequence(event.getSequence() + 1);
        event.setSyncStatus(Event.SyncStatus.PENDING);

        event = eventRepository.save(event);
        return eventMapper.toDTO(event);
    }

    @Transactional
    public void deleteEvent(Long userId, Long eventId) {
        log.info("Deleting event: {} for user: {}", eventId, userId);

        Event event = eventRepository.findByIdAndUserIdAndDeletedFalse(eventId, userId)
            .orElseThrow(() -> new RuntimeException("Event not found"));

        event.setDeleted(true);
        event.setSyncStatus(Event.SyncStatus.PENDING);
        eventRepository.save(event);
    }

    public EventDTO getEvent(Long userId, Long eventId) {
        Event event = eventRepository.findByIdAndUserIdAndDeletedFalse(eventId, userId)
            .orElseThrow(() -> new RuntimeException("Event not found"));
        return eventMapper.toDTO(event);
    }

    public List<EventDTO> getEventsByUserId(Long userId) {
        List<Event> events = eventRepository.findByUserIdAndDeletedFalseOrderByStartTimeAsc(userId);
        return eventMapper.toDTOList(events);
    }

    public List<EventDTO> getEventsByDateRange(Long userId, LocalDateTime start, LocalDateTime end) {
        List<Event> events = eventRepository.findByUserIdAndStartTimeBetweenAndDeletedFalseOrderByStartTimeAsc(userId, start, end);
        return eventMapper.toDTOList(events);
    }

    public PageResponse<EventDTO> getEvents(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("startTime").descending());
        Page<Event> eventPage = eventRepository.findByUserIdAndDeletedFalseOrderByStartTimeDesc(userId, pageable);
        
        List<EventDTO> events = eventMapper.toDTOList(eventPage.getContent());
        
        return PageResponse.of(events, page, size, eventPage.getTotalElements(), eventPage.getTotalPages());
    }

    public PageResponse<EventDTO> searchEvents(Long userId, String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("startTime").descending());
        Page<Event> eventPage = eventRepository.searchEvents(userId, keyword, pageable);
        
        List<EventDTO> events = eventMapper.toDTOList(eventPage.getContent());
        
        return PageResponse.of(events, page, size, eventPage.getTotalElements(), eventPage.getTotalPages());
    }

    public List<EventDTO> getEventsByCategory(Long userId, Event.EventCategory category) {
        List<Event> events = eventRepository.findByUserIdAndCategory(userId, category);
        return eventMapper.toDTOList(events);
    }
}
