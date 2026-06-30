package com.evaap.repository;

import com.evaap.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
    Optional<Profile> findByUserId(Long userId);

    boolean existsByPhoneNumber(String phoneNumber);
}
