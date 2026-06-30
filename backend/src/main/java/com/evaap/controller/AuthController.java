package com.evaap.controller;

import com.evaap.dto.request.LoginRequest;
import com.evaap.dto.request.RefreshTokenRequest;
import com.evaap.dto.request.RegisterRequest;
import com.evaap.dto.response.ApiResponse;
import com.evaap.dto.response.AuthResponse;
import com.evaap.dto.response.UserResponse;
import com.evaap.security.CustomUserDetails;
import com.evaap.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Register, login, token refresh, and session endpoints")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Register a new candidate/employer account")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Account created successfully", response));
    }

    @PostMapping("/login")
    @Operation(summary = "Log in with email and password")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Exchange a refresh token for a new access/refresh token pair")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        AuthResponse response = authService.refresh(request.getRefreshToken());
        return ResponseEntity.ok(ApiResponse.success("Token refreshed", response));
    }

    @PostMapping("/logout")
    @Operation(summary = "Revoke a refresh token (logs the user out of that session)")
    public ResponseEntity<ApiResponse<Void>> logout(@Valid @RequestBody RefreshTokenRequest request) {
        authService.logout(request.getRefreshToken());
        return ResponseEntity.ok(ApiResponse.success("Logged out successfully"));
    }

    @GetMapping("/me")
    @Operation(summary = "Get the currently authenticated user's details")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser(
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        UserResponse response = authService.getCurrentUser(currentUser.getUserId());
        return ResponseEntity.ok(ApiResponse.success("Current user fetched", response));
    }

    // --- Stubbed for now: OTP/email verification deliberately skipped in this phase. ---
    // Accounts are marked ACTIVE + email-verified immediately at registration instead.
    // Wire these up once SMTP (or a transactional email provider) is set up.

    @PostMapping("/verify-email")
    @Operation(summary = "(Not yet implemented) Verify email via OTP")
    public ResponseEntity<ApiResponse<Void>> verifyEmail() {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                .body(ApiResponse.error("Email verification is not enabled yet — accounts are auto-verified at registration."));
    }

    @PostMapping("/resend-otp")
    @Operation(summary = "(Not yet implemented) Resend verification OTP")
    public ResponseEntity<ApiResponse<Void>> resendOtp() {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                .body(ApiResponse.error("OTP flow is not enabled yet — accounts are auto-verified at registration."));
    }
}
