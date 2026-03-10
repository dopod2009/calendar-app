package com.calendar.controller;

import com.calendar.dto.*;
import com.calendar.model.Event;
import com.calendar.security.UserPrincipal;
import com.calendar.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Tag(name = "Events", description = "Event management APIs")
@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @Operation(summary = "Create a new event")
    @PostMapping
    public ResponseEntity<ApiResponse<EventDTO>> createEvent(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody CreateEventRequest request) {
        EventDTO event = eventService.createEvent(principal.getUserId(), request);
        return ResponseEntity.ok(ApiResponse.success("Event created successfully", event));
    }

    @Operation(summary = "Update an existing event")
    @PutMapping("/{eventId}")
    public ResponseEntity<ApiResponse<EventDTO>> updateEvent(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long eventId,
            @Valid @RequestBody UpdateEventRequest request) {
        EventDTO event = eventService.updateEvent(principal.getUserId(), eventId, request);
        return ResponseEntity.ok(ApiResponse.success("Event updated successfully", event));
    }

    @Operation(summary = "Delete an event")
    @DeleteMapping("/{eventId}")
    public ResponseEntity<ApiResponse<Void>> deleteEvent(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long eventId) {
        eventService.deleteEvent(principal.getUserId(), eventId);
        return ResponseEntity.ok(ApiResponse.success("Event deleted successfully", null));
    }

    @Operation(summary = "Get event by ID")
    @GetMapping("/{eventId}")
    public ResponseEntity<ApiResponse<EventDTO>> getEvent(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long eventId) {
        EventDTO event = eventService.getEvent(principal.getUserId(), eventId);
        return ResponseEntity.ok(ApiResponse.success(event));
    }

    @Operation(summary = "Get all events for current user")
    @GetMapping
    public ResponseEntity<ApiResponse<List<EventDTO>>> getAllEvents(
            @AuthenticationPrincipal UserPrincipal principal) {
        List<EventDTO> events = eventService.getEventsByUserId(principal.getUserId());
        return ResponseEntity.ok(ApiResponse.success(events));
    }

    @Operation(summary = "Get events by date range")
    @GetMapping("/range")
    public ResponseEntity<ApiResponse<List<EventDTO>>> getEventsByDateRange(
            @AuthenticationPrincipal UserPrincipal principal,
            @Parameter(description = "Start date time (yyyy-MM-ddTHH:mm:ss)")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @Parameter(description = "End date time (yyyy-MM-ddTHH:mm:ss)")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        List<EventDTO> events = eventService.getEventsByDateRange(principal.getUserId(), start, end);
        return ResponseEntity.ok(ApiResponse.success(events));
    }

    @Operation(summary = "Get paginated events")
    @GetMapping("/page")
    public ResponseEntity<ApiResponse<PageResponse<EventDTO>>> getEventsPage(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        PageResponse<EventDTO> events = eventService.getEvents(principal.getUserId(), page, size);
        return ResponseEntity.ok(ApiResponse.success(events));
    }

    @Operation(summary = "Search events by keyword")
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<PageResponse<EventDTO>>> searchEvents(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        PageResponse<EventDTO> events = eventService.searchEvents(principal.getUserId(), keyword, page, size);
        return ResponseEntity.ok(ApiResponse.success(events));
    }

    @Operation(summary = "Get events by category")
    @GetMapping("/category/{category}")
    public ResponseEntity<ApiResponse<List<EventDTO>>> getEventsByCategory(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Event.EventCategory category) {
        List<EventDTO> events = eventService.getEventsByCategory(principal.getUserId(), category);
        return ResponseEntity.ok(ApiResponse.success(events));
    }
}
