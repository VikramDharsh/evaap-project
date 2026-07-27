package com.evaap.controller;

import com.evaap.dto.request.EducationRequest;
import com.evaap.dto.response.ApiResponse;
import com.evaap.dto.response.EducationResponse;
import com.evaap.security.CustomUserDetails;
import com.evaap.service.EducationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.evaap.service.EducationService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/education")
@RequiredArgsConstructor
@Tag(name = "Education", description = "Manage the authenticated user's education records")
public class EducationController {

    private final EducationService educationService;
@PostMapping
    @Operation(summary = "Add a new education record for the current user")
    public ResponseEntity<ApiResponse<EducationResponse>> addEducation(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @Valid @RequestBody EducationRequest request
    ) {
        EducationResponse response = educationService.addEducation(currentUser.getUserId(), request);
        return ResponseEntity.ok(ApiResponse.success("Education added", response));
    }

    @GetMapping
    @Operation(summary = "Get all education records for the current user")
    public ResponseEntity<ApiResponse<List<EducationResponse>>> getEducationList(
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        List<EducationResponse> response = educationService.getEducationList(currentUser.getUserId());
        return ResponseEntity.ok(ApiResponse.success("Education records fetched", response));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing education record")
    public ResponseEntity<ApiResponse<EducationResponse>> updateEducation(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable Long id,
            @Valid @RequestBody EducationRequest request
    ) {
        EducationResponse response = educationService.updateEducation(currentUser.getUserId(), id, request);
        return ResponseEntity.ok(ApiResponse.success("Education updated", response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an education record")
    public ResponseEntity<ApiResponse<Void>> deleteEducation(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable Long id
    ) {
        educationService.deleteEducation(currentUser.getUserId(), id);
        return ResponseEntity.ok(ApiResponse.success("Education deleted", null));
    }

    @PostMapping("/upload")
    @Operation(summary = "Upload a document for education verification")
    public ResponseEntity<ApiResponse<String>> uploadDocument(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @RequestParam String fileName,
            @RequestParam String fileUrl
    ) {
        String response = educationService.uploadDocument(currentUser.getUserId(), fileName, fileUrl);
        return ResponseEntity.ok(ApiResponse.success("Document uploaded", response));
    }

    @PostMapping("/submit")
    @Operation(summary = "Submit education records for verification")
    public ResponseEntity<ApiResponse<String>> submitForVerification(
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        String response = educationService.submitForVerification(currentUser.getUserId());
        return ResponseEntity.ok(ApiResponse.success("Submitted for verification", response));
    }
    
}