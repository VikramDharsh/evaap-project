package com.evaap.service;

import com.evaap.dto.request.ProfileRequest;
import com.evaap.dto.response.ProfileResponse;
import com.evaap.entity.Profile;
import com.evaap.exception.DuplicateResourceException;
import com.evaap.exception.ResourceNotFoundException;
import com.evaap.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.evaap.entity.User;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepository profileRepository;

    // Fields tracked for completion %. Keep this list in sync with calculateCompletion below.
    private static final int TRACKED_FIELD_COUNT = 10;

    @Transactional(readOnly = true)
    public ProfileResponse getProfile(Long userId) {
        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found for this user"));
        return toResponse(profile);
    }
    @Transactional
    public ProfileResponse createProfile(Long userId, ProfileRequest request) {
        // Check if profile already exists — don't allow duplicates
        if (profileRepository.findByUserId(userId).isPresent()) {
            throw new DuplicateResourceException("Profile already exists for this user. Use PUT to update it.");
        }

        if (request.getPhoneNumber() != null
                && profileRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new DuplicateResourceException("This phone number is already in use by another account");
        }

        User user = new User();
        user.setId(userId);

        Profile profile = Profile.builder()
                .user(user)
                .firstName(request.getFirstName() != null ? request.getFirstName() : "")
                .lastName(request.getLastName() != null ? request.getLastName() : "")
                .phoneNumber(request.getPhoneNumber() != null ? request.getPhoneNumber() : "")
                .middleName(request.getMiddleName())
                .dateOfBirth(request.getDateOfBirth())
                .profession(request.getProfession())
                .bio(request.getBio())
                .addressLine1(request.getAddressLine1())
                .addressLine2(request.getAddressLine2())
                .city(request.getCity())
                .state(request.getState())
                .pincode(request.getPincode())
                .profileCompletionPercentage(0)
                .build();

        profile = profileRepository.save(profile);
        // Recalculate after save so completion % reflects actual data
        profile.setProfileCompletionPercentage(calculateCompletion(profile));
        profile = profileRepository.save(profile);

        return toResponse(profile);
    }

    @Transactional
    public ProfileResponse updateProfile(Long userId, ProfileRequest request) {
        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found for this user"));

        if (request.getPhoneNumber() != null
                && !request.getPhoneNumber().equals(profile.getPhoneNumber())
                && profileRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new DuplicateResourceException("This phone number is already in use by another account");
        }

        // PATCH-style partial update — only overwrite fields actually present in the request.
        applyIfPresent(request.getFirstName(), profile::setFirstName);
        applyIfPresent(request.getMiddleName(), profile::setMiddleName);
        applyIfPresent(request.getLastName(), profile::setLastName);
        applyIfPresent(request.getPhoneNumber(), profile::setPhoneNumber);
        applyIfPresent(request.getDateOfBirth(), profile::setDateOfBirth);
        applyIfPresent(request.getProfession(), profile::setProfession);
        applyIfPresent(request.getBio(), profile::setBio);
        applyIfPresent(request.getAddressLine1(), profile::setAddressLine1);
        applyIfPresent(request.getAddressLine2(), profile::setAddressLine2);
        applyIfPresent(request.getCity(), profile::setCity);
        applyIfPresent(request.getState(), profile::setState);
        applyIfPresent(request.getPincode(), profile::setPincode);

        profile.setProfileCompletionPercentage(calculateCompletion(profile));

        profile = profileRepository.save(profile);
        return toResponse(profile);
    }

    private <T> void applyIfPresent(T value, java.util.function.Consumer<T> setter) {
        if (value != null) {
            setter.accept(value);
        }
    }

    /**
     * Recomputes completion % based on which of the tracked fields are actually
     * filled. Picture upload isn't included here since that's a separate
     * endpoint/feature not yet built — revisit TRACKED_FIELD_COUNT when it is.
     */
    private int calculateCompletion(Profile profile) {
        int filled = 0;
        if (notBlank(profile.getFirstName())) filled++;
        if (notBlank(profile.getLastName())) filled++;
        if (notBlank(profile.getPhoneNumber())) filled++;
        if (profile.getDateOfBirth() != null) filled++;
        if (notBlank(profile.getProfession())) filled++;
        if (notBlank(profile.getBio())) filled++;
        if (notBlank(profile.getAddressLine1())) filled++;
        if (notBlank(profile.getCity())) filled++;
        if (notBlank(profile.getState())) filled++;
        if (notBlank(profile.getPincode())) filled++;

        return Math.round((filled * 100f) / TRACKED_FIELD_COUNT);
    }

    private boolean notBlank(String value) {
        return value != null && !value.isBlank();
    }

    private ProfileResponse toResponse(Profile profile) {
        return ProfileResponse.builder()
                .id(profile.getId())
                .userId(profile.getUser().getId())
                .firstName(profile.getFirstName())
                .middleName(profile.getMiddleName())
                .lastName(profile.getLastName())
                .phoneNumber(profile.getPhoneNumber())
                .dateOfBirth(profile.getDateOfBirth())
                .profession(profile.getProfession())
                .bio(profile.getBio())
                .profilePicture(profile.getProfilePicture())
                .addressLine1(profile.getAddressLine1())
                .addressLine2(profile.getAddressLine2())
                .city(profile.getCity())
                .state(profile.getState())
                .pincode(profile.getPincode())
                .profileCompletionPercentage(profile.getProfileCompletionPercentage())
                .createdAt(profile.getCreatedAt())
                .updatedAt(profile.getUpdatedAt())
                .build();
    }
}
