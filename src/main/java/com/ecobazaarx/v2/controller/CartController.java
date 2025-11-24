package com.ecobazaarx.v2.controller;

import com.ecobazaarx.v2.dto.*;
import com.ecobazaarx.v2.model.Cart;
import com.ecobazaarx.v2.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CUSTOMER')")
public class CartController {

    private final CartService cartService;

    @PostMapping("/add")
    public ResponseEntity<CartResponse> addProductToCart(
            @RequestBody AddToCartRequest request,
            @AuthenticationPrincipal UserDetails currentUser
    ) {
        CartResponse updatedCart = cartService.addProductToCart(request, currentUser);
        return ResponseEntity.ok(updatedCart);
    }

    @GetMapping
    public ResponseEntity<CartResponse> getMyCart(
            @AuthenticationPrincipal UserDetails currentUser
    ) {
        return ResponseEntity.ok(cartService.getCartForUser(currentUser));
    }

    @PutMapping("/update/{itemId}")
    public ResponseEntity<CartResponse> updateCartItem(
            @PathVariable("itemId") Long cartItemId,
            @RequestBody UpdateCartRequest request,
            @AuthenticationPrincipal UserDetails currentUser
    ) {
        CartResponse cart = cartService.updateCartItemQuantity(cartItemId, request.getQuantity(), currentUser);
        return ResponseEntity.ok(cart);
    }

    @DeleteMapping("/remove/{itemId}")
    public ResponseEntity<CartResponse> removeCartItem(
            @PathVariable("itemId") Long cartItemId,
            @AuthenticationPrincipal UserDetails currentUser
    ) {
        CartResponse cart = cartService.removeProductFromCart(cartItemId, currentUser);
        return ResponseEntity.ok(cart);
    }

    @PostMapping("/apply-discount")
    public ResponseEntity<CartResponse> applyDiscount(
            @RequestBody ApplyDiscountRequest request,
            @AuthenticationPrincipal UserDetails currentUser
    ) {
        CartResponse cart = cartService.applyDiscount(request.getDiscountCode(), currentUser);
        return ResponseEntity.ok(cart);
    }

    @GetMapping("/discounts")
    public ResponseEntity<List<DiscountResponse>> getAvailableDiscounts() {
        return ResponseEntity.ok(cartService.getAvailableDiscounts());
    }

    @GetMapping("/shipping-options/{addressId}")
    public ResponseEntity<List<ShippingOptionDto>> getShippingOptions(
            @PathVariable Long addressId,
            @AuthenticationPrincipal UserDetails currentUser
    ) {
        return ResponseEntity.ok(cartService.getShippingOptions(addressId, currentUser));
    }

    @PostMapping("/select-shipping")
    public ResponseEntity<CartResponse> selectShipping(
            @RequestBody SelectShippingRequest request,
            @AuthenticationPrincipal UserDetails currentUser
    ) {
        return ResponseEntity.ok(cartService.selectShippingOption(request, currentUser));
    }
}
