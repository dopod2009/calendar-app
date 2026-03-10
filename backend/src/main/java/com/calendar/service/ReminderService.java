package com.calendar.service;

import com.calendar.dto.ReminderDTO;
import com.calendar.mapper.EventMapper;
import com.calendar.model.Reminder;
import com.calendar.repository.ReminderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReminderService {

    private final ReminderRepository reminderRepository;
    private final EventMapper eventMapper;

    public List<ReminderDTO> getRemindersByEventId(Long eventId) {
        List<Reminder> reminders = reminderRepository.findByEventIdOrderByReminderTimeAsc(eventId);
        return reminders.stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }

    @Transactional
    public void markReminderAsSent(Long reminderId) {
        Reminder reminder = reminderRepository.findById(reminderId)
            .orElseThrow(() -> new RuntimeException("Reminder not found"));
        
        reminder.setSent(true);
        reminder.setSentAt(LocalDateTime.now());
        reminder.setStatus(Reminder.ReminderStatus.SENT);
        reminderRepository.save(reminder);
    }

    @Scheduled(fixedRate = 60000) // Every minute
    @Transactional
    public void processPendingReminders() {
        log.debug("Processing pending reminders");
        
        List<Reminder> pendingReminders = reminderRepository.findAllPendingReminders(LocalDateTime.now());
        
        for (Reminder reminder : pendingReminders) {
            try {
                sendReminderNotification(reminder);
                reminder.setSent(true);
                reminder.setSentAt(LocalDateTime.now());
                reminder.setStatus(Reminder.ReminderStatus.SENT);
                reminderRepository.save(reminder);
            } catch (Exception e) {
                log.error("Failed to send reminder: {}", reminder.getId(), e);
                reminder.setStatus(Reminder.ReminderStatus.FAILED);
                reminderRepository.save(reminder);
            }
        }
    }

    private void sendReminderNotification(Reminder reminder) {
        // TODO: Implement actual notification sending logic
        // This would integrate with FCM (Firebase Cloud Messaging) or similar
        log.info("Sending reminder notification for event: {}", reminder.getEvent().getTitle());
    }

    private ReminderDTO toDTO(Reminder reminder) {
        return ReminderDTO.builder()
            .id(reminder.getId())
            .eventId(reminder.getEvent().getId())
            .reminderTime(reminder.getReminderTime())
            .status(reminder.getStatus())
            .message(reminder.getMessage())
            .sent(reminder.getSent())
            .sentAt(reminder.getSentAt())
            .notificationId(reminder.getNotificationId())
            .createdAt(reminder.getCreatedAt())
            .build();
    }
}
