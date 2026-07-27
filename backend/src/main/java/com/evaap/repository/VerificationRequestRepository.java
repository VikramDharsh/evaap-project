package com.evaap.repository;

import com.evaap.entity.VerificationRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VerificationRequestRepository extends JpaRepository<VerificationRequest, Long> {

    boolean existsByUserIdAndStatus(Long userId, VerificationRequest.Status status);
}