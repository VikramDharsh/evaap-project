package com.evaap.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Schema(description = "Request body for updating a profile. All fields are optional — only fields you include will be updated.")
public class ProfileRequest {

    @Schema(description = "First name", example = "John")
    @Size(min = 1, max = 50, message = "First name must be between 1 and 50 characters")
    private String firstName;

    @Schema(description = "Middle name", example = "Michael")
    @Size(max = 50, message = "Middle name must be at most 50 characters")
    private String middleName;

    @Schema(description = "Last name", example = "Doe")
    @Size(min = 1, max = 50, message = "Last name must be between 1 and 50 characters")
    private String lastName;

    @Schema(description = "Phone number", example = "9876543210")
    @Pattern(regexp = "^[0-9+\\-\\s]{7,15}$", message = "Phone number must be valid")
    private String phoneNumber;

    @Schema(description = "Date of birth in YYYY-MM-DD format", example = "1998-05-15")
    private LocalDate dateOfBirth;

    @Schema(description = "Current profession or job title", example = "Software Engineer")
    @Size(max = 100, message = "Profession must be at most 100 characters")
    private String profession;

    @Schema(description = "Short bio or about section", example = "Passionate backend developer with 2 years of experience.")
    @Size(max = 2000, message = "Bio must be at most 2000 characters")
    private String bio;

    @Schema(description = "Address line 1", example = "123 MG Road")
    @Size(max = 255, message = "Address line 1 must be at most 255 characters")
    private String addressLine1;

    @Schema(description = "Address line 2 (apartment, suite, etc.)", example = "Apt 4B")
    @Size(max = 255, message = "Address line 2 must be at most 255 characters")
    private String addressLine2;

    @Schema(description = "City", example = "Bengaluru")
    @Size(max = 100, message = "City must be at most 100 characters")
    private String city;

    @Schema(description = "State", example = "Karnataka")
    @Size(max = 100, message = "State must be at most 100 characters")
    private String state;

    @Schema(description = "Postal / PIN code", example = "560001")
    @Pattern(regexp = "^[0-9]{4,15}$", message = "Pincode must be numeric")
    private String pincode;
}
