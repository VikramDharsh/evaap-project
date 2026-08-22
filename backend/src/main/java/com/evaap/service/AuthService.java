package com.evaap.service;

import com.evaap.dto.request.LoginRequest;
import com.evaap.dto.request.RegisterRequest;
import com.evaap.dto.request.ResendOtpRequest;
import com.evaap.dto.request.VerifyEmailRequest;
import com.evaap.dto.response.AuthResponse;
import com.evaap.dto.response.UserResponse;
import com.evaap.entity.EmailVerification;
import com.evaap.entity.Profile;
import com.evaap.entity.RefreshToken;
import com.evaap.entity.Role;
import com.evaap.entity.User;
import com.evaap.exception.DuplicateResourceException;
import com.evaap.exception.InvalidCredentialsException;
import com.evaap.exception.InvalidTokenException;
import com.evaap.exception.ResourceNotFoundException;
import com.evaap.repository.EmailVerificationRepository;
import com.evaap.repository.ProfileRepository;
import com.evaap.repository.RefreshTokenRepository;
import com.evaap.repository.RoleRepository;
import com.evaap.repository.UserRepository;
import com.evaap.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final RoleRepository roleRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final EmailVerificationRepository emailVerificationRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final EmailService emailService;

    @Value("${app.jwt.refresh-token-expiry-ms:604800000}")
    private long refreshTokenExpiryMs;

    @Value("${app.otp.expiry-minutes:10}")
    private long otpExpiryMinutes;

    private static final String DEFAULT_ROLE = "CANDIDATE";
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("An account with this email already exists");
        }
        if (profileRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new DuplicateResourceException("An account with this phone number already exists");
        }

        String requestedRole = (request.getRole() == null || request.getRole().isBlank())
                ? DEFAULT_ROLE
                : request.getRole().toUpperCase();

        // ADMIN cannot be self-assigned at registration — that path goes through
        // admin_whitelist + an explicit promotion later. Anyone requesting ADMIN
        // here silently falls back to CANDIDATE rather than erroring, so we don't
        // leak which emails are whitelisted.
        final String roleName = "ADMIN".equals(requestedRole) ? DEFAULT_ROLE : requestedRole;

        Role role = roleRepository.findByRoleName(roleName)
                .orElseThrow(() -> new ResourceNotFoundException("Role not configured: " + roleName));

        User user = User.builder()
                .publicId(UUID.randomUUID().toString())
                .role(role)
                .email(request.getEmail().toLowerCase().trim())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .accountStatus(User.AccountStatus.PENDING) // stays PENDING until OTP verified below
                .isActive(true)
                .build();

        user = userRepository.save(user);

        Profile profile = Profile.builder()
                .user(user)
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phoneNumber(request.getPhoneNumber())
                .profileCompletionPercentage(calculateInitialCompletion(request))
                .build();

        profileRepository.save(profile);

        issueAndSendOtp(user);

        return buildAuthResponse(user, profile);
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail().toLowerCase().trim())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        if (user.getAccountStatus() == User.AccountStatus.PENDING) {
            throw new InvalidCredentialsException("Please verify your email before logging in");
        }

        if (user.getDeletedAt() != null
                || user.getAccountStatus() == User.AccountStatus.SUSPENDED
                || user.getAccountStatus() == User.AccountStatus.DEACTIVATED) {
            throw new InvalidCredentialsException("This account is not active. Contact support.");
        }

        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

        Profile profile = profileRepository.findByUserId(user.getId()).orElse(null);

        return buildAuthResponse(user, profile);
    }

    @Transactional
    public void verifyEmail(VerifyEmailRequest request) {
        User user = userRepository.findByEmail(request.getEmail().toLowerCase().trim())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.getAccountStatus() != User.AccountStatus.PENDING) {
            // Already verified — treat as a no-op success rather than an error,
            // so a double-tap or a stale frontend retry doesn't surface a scary message.
            return;
        }

        EmailVerification verification = emailVerificationRepository
                .findTopByUserIdAndIsVerifiedFalseOrderByCreatedAtDesc(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("No pending verification found. Please request a new code."));

        if (verification.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new InvalidTokenException("This code has expired. Please request a new one.");
        }

        if (!hashToken(request.getOtp()).equals(verification.getOtpHash())) {
            throw new InvalidTokenException("Invalid verification code");
        }

        verification.setIsVerified(true);
        emailVerificationRepository.save(verification);

        user.setAccountStatus(User.AccountStatus.ACTIVE);
        user.setEmailVerifiedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    @Transactional
    public void resendOtp(ResendOtpRequest request) {
        User user = userRepository.findByEmail(request.getEmail().toLowerCase().trim())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.getAccountStatus() != User.AccountStatus.PENDING) {
            return; // already verified — nothing to resend
        }

        // Invalidate any existing unverified OTP so only the newest one is usable.
        emailVerificationRepository.findTopByUserIdAndIsVerifiedFalseOrderByCreatedAtDesc(user.getId())
                .ifPresent(old -> {
                    old.setIsVerified(true);
                    emailVerificationRepository.save(old);
                });

        issueAndSendOtp(user);
    }

    @Transactional
    public AuthResponse refresh(String rawRefreshToken) {
        String tokenHash = hashToken(rawRefreshToken);

        RefreshToken stored = refreshTokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new InvalidTokenException("Invalid refresh token"));

        if (Boolean.TRUE.equals(stored.getRevoked()) || stored.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new InvalidTokenException("Refresh token expired or revoked. Please log in again.");
        }

        User user = stored.getUser();

        // Rotate: revoke the old refresh token and issue a brand new pair.
        // Prevents a leaked refresh token from being replayed indefinitely.
        stored.setRevoked(true);
        refreshTokenRepository.save(stored);

        Profile profile = profileRepository.findByUserId(user.getId()).orElse(null);

        return buildAuthResponse(user, profile);
    }

    @Transactional
    public void logout(String rawRefreshToken) {
        String tokenHash = hashToken(rawRefreshToken);
        refreshTokenRepository.findByTokenHash(tokenHash).ifPresent(token -> {
            token.setRevoked(true);
            refreshTokenRepository.save(token);
        });
    }

    public UserResponse getCurrentUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Profile profile = profileRepository.findByUserId(userId).orElse(null);
        return toUserResponse(user, profile);
    }

    // --- helpers ---

    private void issueAndSendOtp(User user) {
        String rawOtp = generateOtp();

        EmailVerification verification = EmailVerification.builder()
                .user(user)
                .otpHash(hashToken(rawOtp))
                .verificationToken(UUID.randomUUID().toString())
                .expiresAt(LocalDateTime.now().plusMinutes(otpExpiryMinutes))
                .isVerified(false)
                .build();
        emailVerificationRepository.save(verification);

        emailService.sendOtpEmail(user.getEmail(), rawOtp);
    }

    private String generateOtp() {
        int otp = SECURE_RANDOM.nextInt(1_000_000); // 0 - 999999
        return String.format("%06d", otp);
    }

    private AuthResponse buildAuthResponse(User user, Profile profile) {
        String accessToken = jwtTokenProvider.generateAccessToken(
                user.getId(), user.getEmail(), user.getRole().getRoleName());

        String rawRefreshToken = UUID.randomUUID().toString() + UUID.randomUUID();
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .tokenHash(hashToken(rawRefreshToken))
                .expiresAt(LocalDateTime.now().plusNanos(refreshTokenExpiryMs * 1_000_000))
                .revoked(false)
                .build();
        refreshTokenRepository.save(refreshToken);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(rawRefreshToken)
                .tokenType("Bearer")
                .user(toUserResponse(user, profile))
                .build();
    }

    private UserResponse toUserResponse(User user, Profile profile) {
        return UserResponse.builder()
                .id(user.getId())
                .publicId(user.getPublicId())
                .email(user.getEmail())
                .firstName(profile != null ? profile.getFirstName() : null)
                .lastName(profile != null ? profile.getLastName() : null)
                .role(user.getRole().getRoleName())
                .accountStatus(user.getAccountStatus().name())
                .emailVerified(user.getEmailVerifiedAt() != null)
                .lastLoginAt(user.getLastLoginAt())
                .createdAt(user.getCreatedAt())
                .build();
    }

    private int calculateInitialCompletion(RegisterRequest request) {
        // First/last name + phone are captured at register; rest of the profile fields
        // (DOB, profession, bio, address, picture) come later via the profile module.
        // 3 fields filled out of ~10 tracked fields ≈ 30%. Revisit this once the
        // Profile module's update endpoint exists and can recompute it properly.
        return 30;
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