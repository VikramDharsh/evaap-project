package com.evaap.controller;

import com.evaap.dto.request.ProfileRequest;
import com.evaap.dto.response.ApiResponse;
import com.evaap.dto.response.ProfileResponse;
import com.evaap.security.CustomUserDetails;
import com.evaap.service.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/profile")
@RequiredArgsConstructor
@Tag(name = "Profile", description = "View and update the authenticated user's profile")
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping
    @Operation(summary = "Get the current authenticated user's profile")
    public ResponseEntity<ApiResponse<ProfileResponse>> getProfile(
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        ProfileResponse response = profileService.getProfile(currentUser.getUserId());
        return ResponseEntity.ok(ApiResponse.success("Profile fetched", response));
    }

    @PutMapping
    @Operation(summary = "Update the current authenticated user's profile (partial update — only send fields you want to change)")
    public ResponseEntity<ApiResponse<ProfileResponse>> updateProfile(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @Valid @RequestBody ProfileRequest request
    ) {
        ProfileResponse response = profileService.updateProfile(currentUser.getUserId(), request);
        return ResponseEntity.ok(ApiResponse.success("Profile updated", response));
    }
}
