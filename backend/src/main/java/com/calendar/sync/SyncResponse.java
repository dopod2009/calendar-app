package com.calendar.sync;

import com.calendar.dto.EventDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SyncResponse {
    private List<EventDTO> events;
    private List<EventDTO> conflicts;
    private LocalDateTime syncTime;
    private Boolean hasMore;
}
