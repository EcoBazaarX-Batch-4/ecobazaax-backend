package com.ecobazaarx.v2.controller;

import com.ecobazaarx.v2.dto.*;
import com.ecobazaarx.v2.service.InsightService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/insights")
@RequiredArgsConstructor
public class InsightController {

    private final InsightService insightService;

    @GetMapping("/profile")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ProfileInsightResponse> getProfileInsights(
            @AuthenticationPrincipal UserDetails currentUser
    ) {
        ProfileInsightResponse insights = insightService.getProfileInsights(currentUser);
        return ResponseEntity.ok(insights);
    }

    @GetMapping("/seller")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<SellerInsightResponse> getSellerInsights(
            @AuthenticationPrincipal UserDetails currentUser
    ) {
        return ResponseEntity.ok(insightService.getSellerInsights(currentUser));
    }


//    @GetMapping("/seller/sales-history")
//    @PreAuthorize("hasRole('SELLER')")
//    public ResponseEntity<List<SalesHistoryEntryDto>> getSellerSalesHistory(
//            @AuthenticationPrincipal UserDetails currentUser,
//            @RequestParam(defaultValue = "30") int rangeInDays
//    ) {
//        return ResponseEntity.ok(insightService.getSellerSalesHistory(currentUser, rangeInDays));
//    }

    @GetMapping("/seller/product-performance")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<SellerProductPerformanceDto> getSellerProductPerformance(
            @AuthenticationPrincipal UserDetails currentUser
    ) {
        return ResponseEntity.ok(insightService.getProductPerformance(currentUser));
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdminInsightResponse> getAdminInsights() {
        return ResponseEntity.ok(insightService.getAdminInsights());
    }

    @GetMapping("/admin/leaderboards")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdminLeaderboardDto> getAdminLeaderboards() {
        return ResponseEntity.ok(insightService.getAdminLeaderboards());
    }

    @GetMapping(value = "/seller/export-sales", produces = "text/csv")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<String> exportSellerSales(
            @AuthenticationPrincipal UserDetails currentUser,
            HttpServletResponse response
    ) {
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"sales_export.csv\"");

        String csvData = insightService.getSellerSalesCsv(currentUser);
        return ResponseEntity.ok(csvData);
    }

    @GetMapping(value = "/admin/export-all", produces = "text/csv")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> exportAdminSales(
            HttpServletResponse response
    ) {
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"platform_sales_export.csv\"");

        String csvData = insightService.getAdminSalesCsv();
        return ResponseEntity.ok(csvData);
    }
}
