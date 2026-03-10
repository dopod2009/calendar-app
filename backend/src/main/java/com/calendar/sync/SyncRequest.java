package com.calendar.sync;

import com.calendar.dto.EventDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SyncRequest {
    private String lastSyncTime;
    private List<EventDTO> events;
    private String deviceId;
    private String platform;
}
