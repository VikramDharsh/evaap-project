package com.evaap.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@Schema(description = "User account details — never includes password or sensitive internal fields")
public class UserResponse {

    @Schema(description = "Internal auto-increment ID", example = "1")
    private Long id;

    @Schema(description = "Public UUID — safe to expose in URLs and external references", example = "409f04f4-8a68-44dc-8ee3-a2e689531940")
    private String publicId;

    @Schema(description = "Email address", example = "john.doe@example.com")
    private String email;

    @Schema(description = "First name", example = "John")
    private String firstName;

    @Schema(description = "Last name", example = "Doe")
    private String lastName;

    @Schema(description = "Assigned role", example = "CANDIDATE", allowableValues = {"CANDIDATE", "EMPLOYER", "ADMIN"})
    private String role;

    @Schema(description = "Account status", example = "ACTIVE", allowableValues = {"PENDING", "ACTIVE", "SUSPENDED", "DEACTIVATED"})
    private String accountStatus;

    @Schema(description = "Whether the email address has been verified", example = "true")
    private boolean emailVerified;

    @Schema(description = "Timestamp of the most recent login")
    private LocalDateTime lastLoginAt;

    @Schema(description = "Account creation timestamp")
    private LocalDateTime createdAt;
}
