package com.calendar.repository;

import com.calendar.model.Reminder;
import com.calendar.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReminderRepository extends JpaRepository<Reminder, Long> {

    List<Reminder> findByEventIdAndDeletedFalseOrderByReminderTimeAsc(Long eventId);

    List<Reminder> findByEventIdOrderByReminderTimeAsc(Long eventId);

    @Query("SELECT r FROM Reminder r JOIN r.event e WHERE e.user.id = :userId " +
           "AND r.reminderTime <= :now " +
           "AND r.sent = false " +
           "AND e.deleted = false")
    List<Reminder> findPendingReminders(@Param("userId") Long userId, @Param("now") LocalDateTime now);

    @Query("SELECT r FROM Reminder r WHERE r.reminderTime <= :now " +
           "AND r.sent = false " +
           "AND r.status = 'PENDING'")
    List<Reminder> findAllPendingReminders(@Param("now") LocalDateTime now);

    List<Reminder> findByEventIdAndStatus(Long eventId, Reminder.ReminderStatus status);
}
