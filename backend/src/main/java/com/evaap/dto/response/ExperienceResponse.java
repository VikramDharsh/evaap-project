package com.evaap.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExperienceResponse {
    private Long id;
    private Long userId;
    private String companyName;
    private String jobTitle;
    private String employmentType;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean currentlyWorking;
    private String description;
    private LocalDateTime createdAt;
}
