package com.calendar.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParticipantRequest {
    @Email(message = "Invalid email format")
    private String email;

    @Size(max = 50, message = "Name must be less than 50 characters")
    private String name;
}
