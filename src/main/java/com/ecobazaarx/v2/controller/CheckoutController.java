package com.ecobazaarx.v2.controller;

import com.ecobazaarx.v2.dto.CheckoutRequest;
import com.ecobazaarx.v2.dto.CheckoutResponse;
import com.ecobazaarx.v2.service.CheckoutService;
import com.stripe.exception.StripeException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/checkout")
@RequiredArgsConstructor
public class CheckoutController {

    private final CheckoutService checkoutService;

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<CheckoutResponse> placeOrder(
            @RequestBody CheckoutRequest request,
            @AuthenticationPrincipal UserDetails currentUser
    ) throws StripeException {
        CheckoutResponse response = checkoutService.placeOrder(request, currentUser);
        return ResponseEntity.ok(response);
    }
}
