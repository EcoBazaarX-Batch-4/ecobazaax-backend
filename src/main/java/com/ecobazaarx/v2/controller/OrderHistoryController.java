package com.ecobazaarx.v2.controller;

import com.ecobazaarx.v2.dto.OrderDto;
import com.ecobazaarx.v2.service.OrderHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/profile/orders")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CUSTOMER')") 
public class OrderHistoryController {

    private final OrderHistoryService orderHistoryService;

    @GetMapping
    public ResponseEntity<Page<OrderDto>> getMyOrderHistory(
            @AuthenticationPrincipal UserDetails currentUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(orderHistoryService.getMyOrderHistory(currentUser, page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDto> getMyOrderDetails(
            @AuthenticationPrincipal UserDetails currentUser,
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(orderHistoryService.getMyOrderDetails(currentUser, id));
    }
}
