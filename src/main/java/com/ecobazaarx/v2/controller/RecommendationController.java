package com.ecobazaarx.v2.controller;

import com.ecobazaarx.v2.dto.HomepageRecommendationDto;
import com.ecobazaarx.v2.dto.ProductResponseDto;
import com.ecobazaarx.v2.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;

    @GetMapping("/homepage")
    public ResponseEntity<HomepageRecommendationDto> getHomepageRecommendations() {
        HomepageRecommendationDto recommendations = recommendationService.getHomepageRecommendations();
        return ResponseEntity.ok(recommendations);
    }

    @GetMapping("/me/recent")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<List<ProductResponseDto>> getMyRecentlyViewed(
            @AuthenticationPrincipal UserDetails currentUser
    ) {
        return ResponseEntity.ok(recommendationService.getRecentlyViewed(currentUser));
    }
}
