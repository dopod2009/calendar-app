package com.calendar.repository;

import com.calendar.model.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByUserIdAndDeletedFalseOrderByStartTimeAsc(Long userId);

    Optional<Event> findByIdAndUserIdAndDeletedFalse(Long id, Long userId);

    Optional<Event> findByEventIdAndUserIdAndDeletedFalse(String eventId, Long userId);

    List<Event> findByUserIdAndStartTimeBetweenAndDeletedFalseOrderByStartTimeAsc(
        Long userId, LocalDateTime startTime, LocalDateTime endTime);

    Page<Event> findByUserIdAndDeletedFalseOrderByStartTimeDesc(Long userId, Pageable pageable);

    @Query("SELECT e FROM Event e WHERE e.user.id = :userId " +
           "AND e.deleted = false " +
           "AND (e.title LIKE %:keyword% OR e.description LIKE %:keyword% OR e.location LIKE %:keyword%) " +
           "ORDER BY e.startTime DESC")
    Page<Event> searchEvents(@Param("userId") Long userId, @Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT e FROM Event e WHERE e.user.id = :userId " +
           "AND e.deleted = false " +
           "AND e.category = :category " +
           "ORDER BY e.startTime ASC")
    List<Event> findByUserIdAndCategory(@Param("userId") Long userId, @Param("category") Event.EventCategory category);

    @Query("SELECT e FROM Event e WHERE e.user.id = :userId " +
           "AND e.deleted = false " +
           "AND e.syncStatus = :status")
    List<Event> findByUserIdAndSyncStatus(@Param("userId") Long userId, @Param("status") Event.SyncStatus status);

    @Query("SELECT e FROM Event e WHERE e.user.id = :userId " +
           "AND e.deleted = false " +
           "AND e.lastSyncedAt < :since")
    List<Event> findEventsModifiedSince(@Param("userId") Long userId, @Param("since") LocalDateTime since);

    long countByUserIdAndDeletedFalse(Long userId);
}
