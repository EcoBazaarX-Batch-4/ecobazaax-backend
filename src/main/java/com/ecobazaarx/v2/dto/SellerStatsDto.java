package com.ecobazaarx.v2.dto;

import java.math.BigDecimal;

public class SellerStatsDto {
    public final BigDecimal totalRevenue;
    public final long totalProductsSold;
    public final long totalOrders;

    public SellerStatsDto(BigDecimal totalRevenue, long totalProductsSold, long totalOrders) {
        this.totalRevenue = (totalRevenue != null) ? totalRevenue : BigDecimal.ZERO;
        this.totalProductsSold = totalProductsSold;
        this.totalOrders = totalOrders;
    }
}
