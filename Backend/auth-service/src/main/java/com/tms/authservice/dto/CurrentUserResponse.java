package com.tms.authservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Safe user information returned when the frontend restores an existing session.
 * Passwords and JWT values are intentionally not included in this response.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CurrentUserResponse {

    private Integer userId;

    private String username;

    private String role;
}
