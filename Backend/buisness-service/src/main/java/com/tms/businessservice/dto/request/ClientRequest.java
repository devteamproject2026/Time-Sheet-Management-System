package com.tms.businessservice.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data accepted from React or Postman when a Client is created or updated.
 *
 * Database-managed fields such as clientId and createdAt are intentionally
 * excluded so API callers cannot choose or overwrite them.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClientRequest {

    @NotBlank(message = "Client name is required")
    @Size(max = 100, message = "Client name cannot exceed 100 characters")
    private String clientName;

    @Size(max = 100, message = "Company name cannot exceed 100 characters")
    private String companyName;

    @Email(message = "Email must be valid")
    @Size(max = 100, message = "Email cannot exceed 100 characters")
    private String email;

    @Size(max = 15, message = "Contact cannot exceed 15 characters")
    private String contact;

    @Size(max = 255, message = "Address cannot exceed 255 characters")
    private String address;
}
