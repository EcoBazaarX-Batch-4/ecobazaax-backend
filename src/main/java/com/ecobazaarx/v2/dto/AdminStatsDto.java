package com.ecobazaarx.v2.dto;

import java.math.BigDecimal;

public class AdminStatsDto {
    public final BigDecimal totalRevenue;
    public final long totalOrders;

    public AdminStatsDto(BigDecimal totalRevenue, long totalOrders) {
        this.totalRevenue = (totalRevenue != null) ? totalRevenue : BigDecimal.ZERO;
        this.totalOrders = totalOrders;
    }
}
