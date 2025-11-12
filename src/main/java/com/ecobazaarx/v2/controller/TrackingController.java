package com.ecobazaarx.v2.controller;

import com.ecobazaarx.v2.service.TrackingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/tracking")
@RequiredArgsConstructor
public class TrackingController {

    private final TrackingService trackingService;
    @PostMapping("/view/{productId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Void> logProductView(
            @PathVariable Long productId,
            @AuthenticationPrincipal UserDetails currentUser
    ) {
        trackingService.logProductView(productId, currentUser);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }
}
