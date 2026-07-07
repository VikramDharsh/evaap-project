package com.evaap.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
@Schema(description = "Response returned after a successful login or registration")
public class AuthResponse {

    @Schema(description = "Short-lived JWT access token (15 min). Send this in the Authorization header as 'Bearer <token>'.")
    private String accessToken;

    @Schema(description = "Long-lived refresh token (7 days). Use POST /auth/refresh to get a new access token when this expires.")
    private String refreshToken;

    @Schema(description = "Token type — always 'Bearer'", example = "Bearer")
    private String tokenType;

    @Schema(description = "Basic user details")
    private UserResponse user;
}
