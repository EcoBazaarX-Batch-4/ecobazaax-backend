package com.ecobazaarx.v2.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class SellerInsightResponse {
    private BigDecimal totalRevenue;
    private long totalProductsSold;
    private long totalOrders;
    private BigDecimal averageProductCarbon;
    private long totalInventory;
}

