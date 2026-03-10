package com.calendar.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "events", indexes = {
    @Index(name = "idx_event_user", columnList = "user_id"),
    @Index(name = "idx_event_start_time", columnList = "start_time"),
    @Index(name = "idx_event_end_time", columnList = "end_time"),
    @Index(name = "idx_event_sync_status", columnList = "sync_status")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(length = 20)
    private String eventId;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(length = 1000)
    private String description;

    @Column(length = 50)
    private String location;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @Column
    private Boolean allDay = false;

    @Column(length = 20)
    @Enumerated(EnumType.STRING)
    private EventCategory category;

    @Column(length = 50)
    private String color;

    @Column
    @Builder.Default
    private Boolean reminderEnabled = true;

    @Column
    private Integer reminderMinutes;

    @Column
    @Enumerated(EnumType.STRING)
    private RecurrenceType recurrenceType;

    @Column(length = 100)
    private String recurrenceRule;

    @Column
    private Long sequence;

    @Column(length = 50)
    private String source;

    @Column
    @Enumerated(EnumType.STRING)
    private SyncStatus syncStatus;

    @Column
    private LocalDateTime lastSyncedAt;

    @Column(length = 36)
    private String externalEventId;

    @Column
    private Boolean deleted = false;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Reminder> reminders = new ArrayList<>();

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<EventParticipant> participants = new ArrayList<>();

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public enum EventCategory {
        WORK, PERSONAL, FAMILY, SOCIAL, HEALTH, FINANCE, TRAVEL, OTHER
    }

    public enum RecurrenceType {
        NONE, DAILY, WEEKLY, MONTHLY, YEARLY, CUSTOM
    }

    public enum SyncStatus {
        PENDING, SYNCED, CONFLICT, FAILED
    }
}
