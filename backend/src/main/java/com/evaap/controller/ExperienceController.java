
package com.evaap.controller;

import com.evaap.dto.request.ExperienceRequest;
import com.evaap.dto.response.ApiResponse;
import com.evaap.dto.response.ExperienceResponse;
import com.evaap.security.CustomUserDetails;
import com.evaap.service.ExperienceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/experience")
@RequiredArgsConstructor
@Tag(name = "Experience", description = "Manage the authenticated user's work experience")
public class ExperienceController {

    private final ExperienceService experienceService;

    @PostMapping
    @Operation(summary = "Add a new work experience entry")
    public ResponseEntity<ApiResponse<ExperienceResponse>> addExperience(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @Valid @RequestBody ExperienceRequest request
    ) {
        ExperienceResponse response = experienceService.addExperience(currentUser.getUserId(), request);
        return ResponseEntity.ok(ApiResponse.success("Experience added", response));
    }

    @GetMapping
    @Operation(summary = "Get all work experience entries for the current user")
    public ResponseEntity<ApiResponse<List<ExperienceResponse>>> getMyExperiences(
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        List<ExperienceResponse> response = experienceService.getMyExperiences(currentUser.getUserId());
        return ResponseEntity.ok(ApiResponse.success("Experiences fetched", response));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a work experience entry")
    public ResponseEntity<ApiResponse<ExperienceResponse>> updateExperience(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable Long id,
            @Valid @RequestBody ExperienceRequest request
    ) {
        ExperienceResponse response = experienceService.updateExperience(currentUser.getUserId(), id, request);
        return ResponseEntity.ok(ApiResponse.success("Experience updated", response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a work experience entry")
    public ResponseEntity<ApiResponse<Void>> deleteExperience(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable Long id
    ) {
        experienceService.deleteExperience(currentUser.getUserId(), id);
        return ResponseEntity.ok(ApiResponse.success("Experience deleted", null));
    }
}