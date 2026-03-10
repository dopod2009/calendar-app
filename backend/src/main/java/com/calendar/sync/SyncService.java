package com.calendar.sync;

import com.calendar.dto.EventDTO;
import com.calendar.mapper.EventMapper;
import com.calendar.model.Device;
import com.calendar.model.Event;
import com.calendar.model.User;
import com.calendar.repository.DeviceRepository;
import com.calendar.repository.EventRepository;
import com.calendar.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SyncService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final DeviceRepository deviceRepository;
    private final EventMapper eventMapper;

    @Transactional
    public SyncResponse syncEvents(Long userId, SyncRequest request) {
        log.info("Syncing events for user: {}, lastSyncTime: {}", userId, request.getLastSyncTime());

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        LocalDateTime lastSync = request.getLastSyncTime() != null 
            ? LocalDateTime.parse(request.getLastSyncTime()) 
            : LocalDateTime.MIN;

        // Get events modified since last sync
        List<Event> modifiedEvents = eventRepository.findEventsModifiedSince(userId, lastSync);

        // Process client changes
        List<EventDTO> conflicts = processClientChanges(userId, request.getEvents());

        // Update last sync timestamp
        user.setLastSyncTimestamp(System.currentTimeMillis());
        userRepository.save(user);

        return SyncResponse.builder()
            .events(eventMapper.toDTOList(modifiedEvents))
            .conflicts(conflicts)
            .syncTime(LocalDateTime.now())
            .hasMore(modifiedEvents.size() >= 100)
            .build();
    }

    @Transactional
    protected List<EventDTO> processClientChanges(Long userId, List<EventDTO> clientEvents) {
        if (clientEvents == null || clientEvents.isEmpty()) {
            return List.of();
        }

        List<EventDTO> conflicts = new java.util.ArrayList<>();

        for (EventDTO clientEvent : clientEvents) {
            try {
                Event existingEvent = eventRepository.findByEventIdAndUserIdAndDeletedFalse(
                    clientEvent.getEventId(), userId).orElse(null);

                if (existingEvent == null) {
                    // New event from client
                    createEventFromSync(userId, clientEvent);
                } else {
                    // Check for conflicts
                    if (existingEvent.getSequence() > clientEvent.getSequence()) {
                        // Server version is newer - conflict
                        conflicts.add(eventMapper.toDTO(existingEvent));
                    } else {
                        // Client version is newer or equal - update server
                        updateEventFromSync(existingEvent, clientEvent);
                    }
                }
            } catch (Exception e) {
                log.error("Failed to sync event: {}", clientEvent.getEventId(), e);
            }
        }

        return conflicts;
    }

    private void createEventFromSync(Long userId, EventDTO dto) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        Event event = Event.builder()
            .user(user)
            .eventId(dto.getEventId())
            .title(dto.getTitle())
            .description(dto.getDescription())
            .location(dto.getLocation())
            .startTime(dto.getStartTime())
            .endTime(dto.getEndTime())
            .allDay(dto.getAllDay())
            .category(dto.getCategory())
            .color(dto.getColor())
            .reminderEnabled(dto.getReminderEnabled())
            .reminderMinutes(dto.getReminderMinutes())
            .recurrenceType(dto.getRecurrenceType())
            .recurrenceRule(dto.getRecurrenceRule())
            .sequence(dto.getSequence())
            .source(dto.getSource())
            .syncStatus(Event.SyncStatus.SYNCED)
            .lastSyncedAt(LocalDateTime.now())
            .externalEventId(dto.getExternalEventId())
            .deleted(false)
            .build();

        eventRepository.save(event);
    }

    private void updateEventFromSync(Event existingEvent, EventDTO dto) {
        existingEvent.setTitle(dto.getTitle());
        existingEvent.setDescription(dto.getDescription());
        existingEvent.setLocation(dto.getLocation());
        existingEvent.setStartTime(dto.getStartTime());
        existingEvent.setEndTime(dto.getEndTime());
        existingEvent.setAllDay(dto.getAllDay());
        existingEvent.setCategory(dto.getCategory());
        existingEvent.setColor(dto.getColor());
        existingEvent.setReminderEnabled(dto.getReminderEnabled());
        existingEvent.setReminderMinutes(dto.getReminderMinutes());
        existingEvent.setRecurrenceType(dto.getRecurrenceType());
        existingEvent.setRecurrenceRule(dto.getRecurrenceRule());
        existingEvent.setSequence(dto.getSequence());
        existingEvent.setSyncStatus(Event.SyncStatus.SYNCED);
        existingEvent.setLastSyncedAt(LocalDateTime.now());

        eventRepository.save(existingEvent);
    }

    public SyncStatusResponse getSyncStatus(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        long pendingCount = eventRepository.findByUserIdAndSyncStatus(userId, Event.SyncStatus.PENDING).size();
        long conflictCount = eventRepository.findByUserIdAndSyncStatus(userId, Event.SyncStatus.CONFLICT).size();

        return SyncStatusResponse.builder()
            .lastSyncTimestamp(user.getLastSyncTimestamp())
            .pendingChanges((int) pendingCount)
            .conflicts((int) conflictCount)
            .build();
    }

    @Scheduled(fixedRate = 300000) // Every 5 minutes
    @Transactional
    public void autoSyncPendingEvents() {
        log.debug("Auto-syncing pending events");
        
        List<Event> pendingEvents = eventRepository.findAll()
            .stream()
            .filter(e -> e.getSyncStatus() == Event.SyncStatus.PENDING)
            .collect(Collectors.toList());

        for (Event event : pendingEvents) {
            try {
                event.setSyncStatus(Event.SyncStatus.SYNCED);
                event.setLastSyncedAt(LocalDateTime.now());
                eventRepository.save(event);
            } catch (Exception e) {
                log.error("Failed to sync event: {}", event.getId(), e);
                event.setSyncStatus(Event.SyncStatus.FAILED);
                eventRepository.save(event);
            }
        }
    }
}
