package com.calendar.sync;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SyncStatusResponse {
    private Long lastSyncTimestamp;
    private Integer pendingChanges;
    private Integer conflicts;
}
