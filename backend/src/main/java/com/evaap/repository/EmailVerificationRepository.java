package com.evaap.repository;

import com.evaap.entity.EmailVerification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EmailVerificationRepository extends JpaRepository<EmailVerification, Long> {
    Optional<EmailVerification> findTopByUserIdAndIsVerifiedFalseOrderByCreatedAtDesc(Long userId);
    List<EmailVerification> findByUserId(Long userId);
}
