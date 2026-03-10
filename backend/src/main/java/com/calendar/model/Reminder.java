package com.calendar.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "reminders", indexes = {
    @Index(name = "idx_reminder_event", columnList = "event_id"),
    @Index(name = "idx_reminder_time", columnList = "reminder_time"),
    @Index(name = "idx_reminder_status", columnList = "status")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Reminder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Column(nullable = false)
    private LocalDateTime reminderTime;

    @Column(length = 20)
    @Enumerated(EnumType.STRING)
    private ReminderStatus status;

    @Column(length = 500)
    private String message;

    @Column
    private Boolean sent = false;

    @Column
    private LocalDateTime sentAt;

    @Column(length = 36)
    private String notificationId;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public enum ReminderStatus {
        PENDING, SENT, CANCELLED, FAILED
    }
}
