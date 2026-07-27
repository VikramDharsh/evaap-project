package com.evaap.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Schema(description = "Request body for adding or updating an education record")
public class EducationRequest {

    @Schema(description = "Institution name", example = "PES University")
    @NotBlank(message = "Institution name is required")
    @Size(max = 255, message = "Institution name must be at most 255 characters")
    private String institutionName;

    @Schema(description = "Qualification level", example = "Bachelors")
    @Size(max = 100, message = "Qualification must be at most 100 characters")
    private String qualification;

    @Schema(description = "Degree name", example = "B.Tech")
    @Size(max = 150, message = "Degree name must be at most 150 characters")
    private String degreeName;

    @Schema(description = "Specialization", example = "Computer Science")
    @Size(max = 150, message = "Specialization must be at most 150 characters")
    private String specialization;

    @Schema(description = "Start year", example = "2021")
    private Integer startYear;

    @Schema(description = "End year", example = "2025")
    private Integer endYear;

    @Schema(description = "CGPA", example = "8.75")
    private BigDecimal cgpa;

    @Schema(description = "Whether currently studying here", example = "false")
    private Boolean currentlyStudying;
}
