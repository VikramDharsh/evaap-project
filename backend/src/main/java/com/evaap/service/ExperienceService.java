package com.evaap.service;

import com.evaap.dto.request.ExperienceRequest;
import com.evaap.dto.response.ExperienceResponse;
import com.evaap.entity.Experience;
import com.evaap.entity.User;
import com.evaap.exception.ResourceNotFoundException;
import com.evaap.repository.ExperienceRepository;
import com.evaap.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExperienceService {

    private final ExperienceRepository experienceRepository;
    private final UserRepository userRepository;

    @Transactional
    public ExperienceResponse addExperience(Long userId, ExperienceRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Experience experience = Experience.builder()
                .user(user)
                .companyName(request.getCompanyName())
                .jobTitle(request.getJobTitle())
                .employmentType(request.getEmploymentType())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .currentlyWorking(request.getCurrentlyWorking() != null ? request.getCurrentlyWorking() : false)
                .description(request.getDescription())
                .build();

        experience = experienceRepository.save(experience);
        return toResponse(experience);
    }

    @Transactional(readOnly = true)
    public List<ExperienceResponse> getMyExperiences(Long userId) {
        return experienceRepository.findByUserId(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public ExperienceResponse updateExperience(Long userId, Long experienceId, ExperienceRequest request) {
        Experience experience = experienceRepository.findByIdAndUserId(experienceId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Experience not found for this user"));

        experience.setCompanyName(request.getCompanyName());
        experience.setJobTitle(request.getJobTitle());
        experience.setEmploymentType(request.getEmploymentType());
        experience.setStartDate(request.getStartDate());
        experience.setEndDate(request.getEndDate());
        if (request.getCurrentlyWorking() != null) {
            experience.setCurrentlyWorking(request.getCurrentlyWorking());
        }
        experience.setDescription(request.getDescription());

        experience = experienceRepository.save(experience);
        return toResponse(experience);
    }

    @Transactional
    public void deleteExperience(Long userId, Long experienceId) {
        Experience experience = experienceRepository.findByIdAndUserId(experienceId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Experience not found for this user"));
        experienceRepository.delete(experience);
    }

    private ExperienceResponse toResponse(Experience experience) {
        return ExperienceResponse.builder()
                .id(experience.getId())
                .userId(experience.getUser().getId())
                .companyName(experience.getCompanyName())
                .jobTitle(experience.getJobTitle())
                .employmentType(experience.getEmploymentType())
                .startDate(experience.getStartDate())
                .endDate(experience.getEndDate())
                .currentlyWorking(experience.getCurrentlyWorking())
                .description(experience.getDescription())
                .createdAt(experience.getCreatedAt())
                .build();
    }
}
