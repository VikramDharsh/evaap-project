package com.evaap.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@Schema(description = "Full profile details for a user")
public class ProfileResponse {

    @Schema(description = "Profile record ID", example = "1")
    private Long id;

    @Schema(description = "The user this profile belongs to", example = "1")
    private Long userId;

    @Schema(example = "John")
    private String firstName;

    @Schema(example = "Michael")
    private String middleName;

    @Schema(example = "Doe")
    private String lastName;

    @Schema(example = "9876543210")
    private String phoneNumber;

    @Schema(description = "Date of birth", example = "1998-05-15")
    private LocalDate dateOfBirth;

    @Schema(example = "Software Engineer")
    private String profession;

    @Schema(example = "Passionate backend developer.")
    private String bio;

    @Schema(description = "URL to the profile picture (stored in LOCAL or AWS_S3)")
    private String profilePicture;

    @Schema(example = "123 MG Road")
    private String addressLine1;

    @Schema(example = "Apt 4B")
    private String addressLine2;

    @Schema(example = "Bengaluru")
    private String city;

    @Schema(example = "Karnataka")
    private String state;

    @Schema(example = "560001")
    private String pincode;

    @Schema(description = "Profile completion percentage (0–100), recalculated on every update", example = "60")
    private Integer profileCompletionPercentage;

    @Schema(description = "Profile creation timestamp")
    private LocalDateTime createdAt;

    @Schema(description = "Last updated timestamp")
    private LocalDateTime updatedAt;
}
