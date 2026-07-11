package com.evaap.security;

import com.evaap.entity.Profile;
import com.evaap.entity.Role;
import com.evaap.entity.User;
import com.evaap.repository.ProfileRepository;
import com.evaap.repository.RoleRepository;
import com.evaap.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Called by Spring Security after Google successfully authenticates the user
 * and hands back their profile info (email, name, picture).
 *
 * Flow:
 *  1. Google redirects back with an auth code
 *  2. Spring Security exchanges that code for an access token + user info
 *  3. This service receives the user info as an OAuth2User
 *  4. We find or create the user in our DB
 *  5. We wrap them in CustomUserDetails so our JWT filter can work with them
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final RoleRepository roleRepository;

    private static final String DEFAULT_ROLE = "CANDIDATE";

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // Delegate to Spring's default implementation to fetch the user info from Google
        OAuth2User oAuth2User = new DefaultOAuth2UserService().loadUser(userRequest);

        String email = oAuth2User.getAttribute("email");
        String firstName = oAuth2User.getAttribute("given_name");
        String lastName = oAuth2User.getAttribute("family_name");
        String picture = oAuth2User.getAttribute("picture");

        if (email == null) {
            throw new OAuth2AuthenticationException("Email not returned by Google — ensure the 'email' scope is requested");
        }

        // Find existing user or create a new one
        User user = userRepository.findByEmail(email.toLowerCase())
                .orElseGet(() -> createNewOAuth2User(email, firstName, lastName, picture));

        log.info("OAuth2 login: {} via Google", email);

        return new CustomUserDetails(user);
    }

    private User createNewOAuth2User(String email, String firstName, String lastName, String picture) {
        Role role = roleRepository.findByRoleName(DEFAULT_ROLE)
                .orElseThrow(() -> new IllegalStateException("Default role CANDIDATE not found in DB"));

        User user = User.builder()
                .publicId(UUID.randomUUID().toString())
                .role(role)
                .email(email.toLowerCase())
                .passwordHash("OAUTH2_NO_PASSWORD") // placeholder — OAuth2 users never use password login
                .accountStatus(User.AccountStatus.ACTIVE)
                .emailVerifiedAt(LocalDateTime.now()) // Google already verified the email
                .isActive(true)
                .build();

        user = userRepository.save(user);

        Profile profile = Profile.builder()
                .user(user)
                .firstName(firstName != null ? firstName : "")
                .lastName(lastName != null ? lastName : "")
                .phoneNumber("") // not provided by Google — user fills this in later
                .profilePicture(picture)
                .profileCompletionPercentage(20) // name + picture but no phone/address yet
                .build();

        profileRepository.save(profile);

        log.info("Created new user via Google OAuth2: {}", email);
        return user;
    }
}
