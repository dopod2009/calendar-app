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
@Table(name = "event_participants", indexes = {
    @Index(name = "idx_participant_event", columnList = "event_id"),
    @Index(name = "idx_participant_email", columnList = "email")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class EventParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Column(length = 100)
    private String email;

    @Column(length = 50)
    private String name;

    @Column
    @Enumerated(EnumType.STRING)
    private ParticipantStatus status;

    @Column
    private LocalDateTime respondedAt;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public enum ParticipantStatus {
        PENDING, ACCEPTED, DECLINED, TENTATIVE
    }
}
