package com.calendar.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String email;
    private String username;
    private String phone;
    private String gender;
    private String avatar;
    private String timezone;
    private Boolean active;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
