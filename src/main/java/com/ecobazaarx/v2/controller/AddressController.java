package com.ecobazaarx.v2.controller;

import com.ecobazaarx.v2.dto.AddressDto;
import com.ecobazaarx.v2.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/profile/addresses")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CUSTOMER')")
public class AddressController {

    private final AddressService addressService;

    @GetMapping
    public ResponseEntity<List<AddressDto>> getMyAddresses(
            @AuthenticationPrincipal UserDetails currentUser
    ) {
        return ResponseEntity.ok(addressService.getMyAddresses(currentUser));
    }

    @PostMapping
    public ResponseEntity<AddressDto> createAddress(
            @RequestBody AddressDto dto,
            @AuthenticationPrincipal UserDetails currentUser
    ) {
        return ResponseEntity.ok(addressService.createAddress(currentUser, dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AddressDto> getAddressById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails currentUser
    ) {
        return ResponseEntity.ok(addressService.getAddressById(currentUser, id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AddressDto> updateAddress(
            @PathVariable Long id,
            @RequestBody AddressDto dto,
            @AuthenticationPrincipal UserDetails currentUser
    ) {
        return ResponseEntity.ok(addressService.updateAddress(currentUser, id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAddress(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails currentUser
    ) {
        addressService.deleteAddress(currentUser, id);
        return ResponseEntity.noContent().build();
    }
}
