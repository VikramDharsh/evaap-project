package com.evaap.repository;

import com.evaap.entity.Experience;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ExperienceRepository extends JpaRepository<Experience, Long> {
    List<Experience> findByUserId(Long userId);

    Optional<Experience> findByIdAndUserId(Long id, Long userId);
}
