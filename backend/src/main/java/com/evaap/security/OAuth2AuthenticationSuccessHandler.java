package com.evaap.security;

import com.evaap.entity.RefreshToken;
import com.evaap.entity.User;
import com.evaap.repository.RefreshTokenRepository;
import com.evaap.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.UUID;

/**
 * Called after Google OAuth2 completes successfully.
 * Issues our own JWT access + refresh tokens and redirects to the frontend
 * with the tokens as query parameters so the frontend can store them.
 *
 * Frontend receives: http://localhost:5173/auth/callback?token=<jwt>&refreshToken=<token>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    @Value("${app.oauth2.redirect-uri:http://localhost:5173/auth/callback}")
    private String frontendRedirectUri;

    @Value("${app.jwt.refresh-token-expiry-ms:604800000}")
    private long refreshTokenExpiryMs;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        // Generate JWT access token
        String accessToken = jwtTokenProvider.generateAccessToken(
                userDetails.getUserId(),
                userDetails.getEmail(),
                userDetails.getRoleName()
        );

        // Issue and store a hashed refresh token
        User user = userRepository.getReferenceById(userDetails.getUserId());
        String rawRefreshToken = UUID.randomUUID().toString() + UUID.randomUUID();
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .tokenHash(hashToken(rawRefreshToken))
                .expiresAt(LocalDateTime.now().plusNanos(refreshTokenExpiryMs * 1_000_000))
                .revoked(false)
                .build();
        refreshTokenRepository.save(refreshToken);

        // Redirect to frontend with tokens in URL
        String redirectUrl = UriComponentsBuilder.fromUriString(frontendRedirectUri)
                .queryParam("token", accessToken)
                .queryParam("refreshToken", rawRefreshToken)
                .build().toUriString();

        log.info("OAuth2 success for user: {}, redirecting to frontend", userDetails.getEmail());
        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }

    private String hashToken(String rawToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(rawToken.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }
}
