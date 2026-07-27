package com.evaap.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@Schema(description = "Education record details")
public class EducationResponse {

    @Schema(description = "Education record ID", example = "1")
    private Long id;

    @Schema(description = "The user this record belongs to", example = "1")
    private Long userId;

    @Schema(example = "PES University")
    private String institutionName;

    @Schema(example = "Bachelors")
    private String qualification;

    @Schema(example = "B.Tech")
    private String degreeName;

    @Schema(example = "Computer Science")
    private String specialization;

    @Schema(example = "2021")
    private Integer startYear;

    @Schema(example = "2025")
    private Integer endYear;

    @Schema(example = "8.75")
    private BigDecimal cgpa;

    @Schema(example = "false")
    private Boolean currentlyStudying;

    @Schema(description = "Record creation timestamp")
    private LocalDateTime createdAt;
}