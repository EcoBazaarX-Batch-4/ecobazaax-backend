package com.ecobazaarx.v2.dto;

import java.math.BigDecimal;

public class SellerStatsDto {
    // These must match the types and order in the COALESCE query
    public final BigDecimal totalRevenue;
    public final long totalProductsSold;
    public final long totalOrders;

    public SellerStatsDto(BigDecimal totalRevenue, Long totalProductsSold, Long totalOrders) {
        this.totalRevenue = (totalRevenue != null) ? totalRevenue : BigDecimal.ZERO;
        this.totalProductsSold = (totalProductsSold != null) ? totalProductsSold : 0L;
        this.totalOrders = (totalOrders != null) ? totalOrders : 0L;
    }
}