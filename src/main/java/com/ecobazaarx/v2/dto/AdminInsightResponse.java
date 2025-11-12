package com.ecobazaarx.v2.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class AdminInsightResponse {
    private long totalCustomers;
    private long totalSellers;
    private long totalProducts;
    private BigDecimal totalPlatformRevenue;
    private long totalOrders;
    private BigDecimal siteWideAverageCarbon;
}
