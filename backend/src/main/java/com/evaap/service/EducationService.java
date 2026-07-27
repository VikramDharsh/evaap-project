package com.evaap.service;

import com.evaap.dto.request.EducationRequest;
import com.evaap.dto.response.EducationResponse;
import com.evaap.entity.Document;
import com.evaap.entity.Education;
import com.evaap.entity.User;
import com.evaap.entity.VerificationRequest;
import com.evaap.exception.DuplicateResourceException;
import com.evaap.exception.ResourceNotFoundException;
import com.evaap.repository.DocumentRepository;
import com.evaap.repository.EducationRepository;
import com.evaap.repository.UserRepository;
import com.evaap.repository.VerificationRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
public class EducationService {

   private final EducationRepository educationRepository;
    private final UserRepository userRepository;
    private final DocumentRepository documentRepository;
    private final VerificationRequestRepository verificationRequestRepository;

    @Transactional
    public EducationResponse addEducation(Long userId, EducationRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Education education = Education.builder()
                .user(user)
                .institutionName(request.getInstitutionName())
                .qualification(request.getQualification())
                .degreeName(request.getDegreeName())
                .specialization(request.getSpecialization())
                .startYear(request.getStartYear())
                .endYear(request.getEndYear())
                .cgpa(request.getCgpa())
                .currentlyStudying(request.getCurrentlyStudying() != null ? request.getCurrentlyStudying() : false)
                .build();

        education = educationRepository.save(education);

        return toResponse(education);
    }
    @Transactional(readOnly = true)
    public List<EducationResponse> getEducationList(Long userId) {
        List<Education> educationList = educationRepository.findByUserId(userId);

        return educationList.stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public EducationResponse updateEducation(Long userId, Long educationId, EducationRequest request) {
        Education education = educationRepository.findByIdAndUserId(educationId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Education record not found"));

        education.setInstitutionName(request.getInstitutionName());
        education.setQualification(request.getQualification());
        education.setDegreeName(request.getDegreeName());
        education.setSpecialization(request.getSpecialization());
        education.setStartYear(request.getStartYear());
        education.setEndYear(request.getEndYear());
        education.setCgpa(request.getCgpa());
        education.setCurrentlyStudying(request.getCurrentlyStudying() != null ? request.getCurrentlyStudying() : false);

        education = educationRepository.save(education);

        return toResponse(education);
    }

    @Transactional
    public void deleteEducation(Long userId, Long educationId) {
        Education education = educationRepository.findByIdAndUserId(educationId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Education record not found"));

        educationRepository.delete(education);
    }

    @Transactional
    public String uploadDocument(Long userId, String fileName, String fileUrl) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Document document = Document.builder()
                .user(user)
                .documentType("EDUCATION")
                .fileName(fileName)
                .fileUrl(fileUrl)
                .build();

        documentRepository.save(document);

        return "Document uploaded successfully";
    }

    @Transactional
    public String submitForVerification(Long userId) {
        boolean alreadyPending = verificationRequestRepository
                .existsByUserIdAndStatus(userId, VerificationRequest.Status.PENDING);

        if (alreadyPending) {
            throw new DuplicateResourceException("A verification request is already pending");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        VerificationRequest request = VerificationRequest.builder()
                .user(user)
                .build();

        verificationRequestRepository.save(request);

        return "Verification request submitted";
    }


    private EducationResponse toResponse(Education education) {
        return EducationResponse.builder()
                .id(education.getId())
                .userId(education.getUser().getId())
                .institutionName(education.getInstitutionName())
                .qualification(education.getQualification())
                .degreeName(education.getDegreeName())
                .specialization(education.getSpecialization())
                .startYear(education.getStartYear())
                .endYear(education.getEndYear())
                .cgpa(education.getCgpa())
                .currentlyStudying(education.getCurrentlyStudying())
                .createdAt(education.getCreatedAt())
                .build();
    }

}