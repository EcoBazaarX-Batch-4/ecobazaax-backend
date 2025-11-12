package com.ecobazaarx.v2.controller;

import com.ecobazaarx.v2.dto.ChangePasswordRequest;
import com.ecobazaarx.v2.dto.ProfileResponse;
import com.ecobazaarx.v2.dto.ProfileUpdateRequest;
import com.ecobazaarx.v2.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping("/me")
    public ResponseEntity<ProfileResponse> getMyProfile(
            @AuthenticationPrincipal UserDetails currentUser
    ) {
        return ResponseEntity.ok(profileService.getProfile(currentUser));
    }

    @PutMapping("/me")
    public ResponseEntity<ProfileResponse> updateMyProfile(
            @RequestBody ProfileUpdateRequest request,
            @AuthenticationPrincipal UserDetails currentUser
    ) {
        return ResponseEntity.ok(profileService.updateProfile(currentUser, request));
    }

    @PutMapping("/change-password")
    public ResponseEntity<Void> changePassword(
            @RequestBody ChangePasswordRequest request,
            @AuthenticationPrincipal UserDetails currentUser
    ) {
        profileService.changePassword(currentUser, request);
        return ResponseEntity.ok().build();
    }
}
