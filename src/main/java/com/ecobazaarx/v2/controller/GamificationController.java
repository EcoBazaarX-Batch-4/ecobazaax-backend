package com.ecobazaarx.v2.controller;

import com.ecobazaarx.v2.dto.EcoPointLedgerDto;
import com.ecobazaarx.v2.service.GamificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/profile")
@RequiredArgsConstructor
public class GamificationController {

    private final GamificationService gamificationService;
    @GetMapping("/eco-points-history")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Page<EcoPointLedgerDto>> getMyPointHistory(
            @AuthenticationPrincipal UserDetails currentUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("transactionDate").descending());

        return ResponseEntity.ok(gamificationService.getPointHistory(currentUser, pageable));
    }
}
