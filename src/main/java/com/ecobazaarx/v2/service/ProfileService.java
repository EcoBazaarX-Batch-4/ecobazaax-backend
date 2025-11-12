package com.ecobazaarx.v2.service;

import com.ecobazaarx.v2.dto.ChangePasswordRequest;
import com.ecobazaarx.v2.dto.ProfileResponse;
import com.ecobazaarx.v2.dto.ProfileUpdateRequest;
import com.ecobazaarx.v2.model.User;
import com.ecobazaarx.v2.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public ProfileResponse getProfile(UserDetails currentUser) {
        User user = findUserByEmail(currentUser.getUsername());
        return mapUserToProfileResponse(user);
    }

    @Transactional
    public ProfileResponse updateProfile(UserDetails currentUser, ProfileUpdateRequest request) {
        User user = findUserByEmail(currentUser.getUsername());

        user.setName(request.getName());
        user.setEmail(request.getEmail());
        User updatedUser = userRepository.save(user);
        return mapUserToProfileResponse(updatedUser);
    }

    @Transactional
    public void changePassword(UserDetails currentUser, ChangePasswordRequest request) {
        User user = findUserByEmail(currentUser.getUsername());

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Incorrect current password");
        }

        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new IllegalArgumentException("New password cannot be the same as the old password");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    ProfileResponse mapUserToProfileResponse(User user) {
        return ProfileResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .roles(user.getRoles().stream()
                        .map(role -> role.getName())
                        .collect(Collectors.toSet()))
                .ecoPoints(user.getEcoPoints())
                .rankLevel(user.getRankLevel())
                .lifetimeAverageCarbon(user.getLifetimeAverageCarbon())
                .sellerStatus(user.getSellerStatus())
                .referralCode(user.getReferralCode())
                .build();
    }
}
