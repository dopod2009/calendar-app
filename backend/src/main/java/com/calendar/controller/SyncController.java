package com.calendar.controller;

import com.calendar.dto.ApiResponse;
import com.calendar.security.UserPrincipal;
import com.calendar.sync.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Sync", description = "Data synchronization APIs")
@RestController
@RequestMapping("/sync")
@RequiredArgsConstructor
public class SyncController {

    private final SyncService syncService;

    @Operation(summary = "Sync events between client and server")
    @PostMapping("/events")
    public ResponseEntity<ApiResponse<SyncResponse>> syncEvents(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody SyncRequest request) {
        SyncResponse response = syncService.syncEvents(principal.getUserId(), request);
        return ResponseEntity.ok(ApiResponse.success("Sync completed", response));
    }

    @Operation(summary = "Get sync status")
    @GetMapping("/status")
    public ResponseEntity<ApiResponse<SyncStatusResponse>> getSyncStatus(
            @AuthenticationPrincipal UserPrincipal principal) {
        SyncStatusResponse status = syncService.getSyncStatus(principal.getUserId());
        return ResponseEntity.ok(ApiResponse.success(status));
    }
}
