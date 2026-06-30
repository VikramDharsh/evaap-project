package com.evaap.dto.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * All fields optional — this powers a partial update (PUT semantics here
 * behave like PATCH: only non-null fields are applied). Validation only
 * fires on fields that are actually present in the request.
 */
@Getter
@Setter
public class ProfileRequest {

    @Size(min = 1, max = 50, message = "First name must be between 1 and 50 characters")
    private String firstName;

    @Size(max = 50, message = "Middle name must be at most 50 characters")
    private String middleName;

    @Size(min = 1, max = 50, message = "Last name must be between 1 and 50 characters")
    private String lastName;

    @Pattern(regexp = "^[0-9+\\-\\s]{7,15}$", message = "Phone number must be valid")
    private String phoneNumber;

    private LocalDate dateOfBirth;

    @Size(max = 100, message = "Profession must be at most 100 characters")
    private String profession;

    @Size(max = 2000, message = "Bio must be at most 2000 characters")
    private String bio;

    @Size(max = 255, message = "Address line 1 must be at most 255 characters")
    private String addressLine1;

    @Size(max = 255, message = "Address line 2 must be at most 255 characters")
    private String addressLine2;

    @Size(max = 100, message = "City must be at most 100 characters")
    private String city;

    @Size(max = 100, message = "State must be at most 100 characters")
    private String state;

    @Pattern(regexp = "^[0-9]{4,15}$", message = "Pincode must be numeric")
    private String pincode;
}
