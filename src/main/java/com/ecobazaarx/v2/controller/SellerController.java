package com.ecobazaarx.v2.controller;

import com.ecobazaarx.v2.dto.*;
import com.ecobazaarx.v2.model.SellerApplicationStatus;
import com.ecobazaarx.v2.service.ProductService;
import com.ecobazaarx.v2.service.SellerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/seller")
@RequiredArgsConstructor
public class SellerController {

    private final SellerService sellerService;
    private final ProductService productService;

    @PostMapping("/apply")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ProfileResponse> applyToBeSeller(
            @RequestBody SellerApplicationRequest request,
            @AuthenticationPrincipal UserDetails currentUser
    ) {
        if (currentUser.getAuthorities().size() > 1 ||
                !currentUser.getAuthorities().iterator().next().getAuthority().equals("ROLE_CUSTOMER")) {
            throw new org.springframework.security.access.AccessDeniedException("Only customers can apply.");
        }

        ProfileResponse updatedProfile = sellerService.applyToBeSeller(currentUser, request);
        return ResponseEntity.ok(updatedProfile);
    }

    @GetMapping("/application-status")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<SellerApplicationStatus> getMyApplicationStatus(
            @AuthenticationPrincipal UserDetails currentUser
    ) {
        return ResponseEntity.ok(sellerService.getApplicationStatus(currentUser));
    }

    @PostMapping("/products")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ProductResponseDto> createProduct(
            @RequestBody ProductCreateRequest request,
            @AuthenticationPrincipal UserDetails sellerDetails
    ) {
        ProductResponseDto newProduct = productService.createProduct(request, sellerDetails);
        return ResponseEntity.status(HttpStatus.CREATED).body(newProduct);
    }

    @GetMapping("/products")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<Page<ProductResponseDto>> getMyProducts(
            @AuthenticationPrincipal UserDetails sellerDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,desc") String[] sort
    ) {
        String sortField = sort[0];
        Sort.Direction sortDirection = (sort.length > 1 && sort[1].equalsIgnoreCase("asc")) ?
                Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortField));

        return ResponseEntity.ok(productService.getProductsBySeller(sellerDetails, pageable));
    }

    @PutMapping("/products/{id}")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ProductResponseDto> updateMyProduct(
            @PathVariable Long id,
            @RequestBody ProductCreateRequest request,
            @AuthenticationPrincipal UserDetails sellerDetails
    ) {
        ProductResponseDto updatedProduct = productService.updateSellerProduct(id, request, sellerDetails);
        return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping("/products/{id}")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<Void> deleteMyProduct(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails sellerDetails
    ) {
        productService.deleteSellerProduct(id, sellerDetails);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/profile")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ProfileResponse> updateMyStoreProfile(
            @RequestBody StoreProfileRequest request,
            @AuthenticationPrincipal UserDetails sellerDetails
    ) {
        ProfileResponse updatedProfile = sellerService.updateStoreProfile(sellerDetails, request);
        return ResponseEntity.ok(updatedProfile);
    }

    @GetMapping("/payout-details")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<PayoutDetailsDto> getMyPayoutDetails(
            @AuthenticationPrincipal UserDetails sellerDetails
    ) {
        return ResponseEntity.ok(sellerService.getPayoutDetails(sellerDetails));
    }

    @PutMapping("/payout-details")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<PayoutDetailsDto> updateMyPayoutDetails(
            @RequestBody PayoutDetailsDto request,
            @AuthenticationPrincipal UserDetails sellerDetails
    ) {
        return ResponseEntity.ok(sellerService.updatePayoutDetails(sellerDetails, request));
    }
}
