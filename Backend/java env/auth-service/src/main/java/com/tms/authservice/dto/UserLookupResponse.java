package com.tms.authservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Safe, small user response used by HR dropdowns.
 *
 * Password, account controls, and other private fields are deliberately not
 * returned. The frontend needs only an ID and display information.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserLookupResponse {

    private Integer userId;
    private String username;
    private String fullName;
    private String email;
}
