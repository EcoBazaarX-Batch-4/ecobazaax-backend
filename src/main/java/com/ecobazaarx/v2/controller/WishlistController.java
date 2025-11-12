package com.ecobazaarx.v2.controller;

import com.ecobazaarx.v2.dto.ProductResponseDto;
import com.ecobazaarx.v2.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/profile/wishlist")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CUSTOMER')")
public class WishlistController {

    private final WishlistService wishlistService;

    @GetMapping
    public ResponseEntity<List<ProductResponseDto>> getMyWishlist(
            @AuthenticationPrincipal UserDetails currentUser
    ) {
        return ResponseEntity.ok(wishlistService.getWishlist(currentUser));
    }

    @PostMapping("/{productId}")
    public ResponseEntity<ProductResponseDto> addToWishlist(
            @PathVariable Long productId,
            @AuthenticationPrincipal UserDetails currentUser
    ) {
        return ResponseEntity.ok(wishlistService.addProductToWishlist(currentUser, productId));
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> removeFromWishlist(
            @PathVariable Long productId,
            @AuthenticationPrincipal UserDetails currentUser
    ) {
        wishlistService.removeProductFromWishlist(currentUser, productId);
        return ResponseEntity.noContent().build();
    }
}
