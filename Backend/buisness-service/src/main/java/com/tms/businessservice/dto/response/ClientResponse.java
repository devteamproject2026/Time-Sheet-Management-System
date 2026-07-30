package com.tms.businessservice.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Safe Client data returned by the Business Service API.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientResponse {

    private Integer clientId;

    private String clientName;

    private String companyName;

    private String email;

    private String contact;

    private String address;

    private LocalDateTime createdAt;
}
