package com.ecobazaarx.v2.dto;

import java.math.BigDecimal;

public class SalesHistoryEntryDto {
    public final String period;
    public final BigDecimal totalRevenue;
    public final long totalOrders;

    public SalesHistoryEntryDto(String period, BigDecimal totalRevenue, long totalOrders) {
        this.period = period;
        this.totalRevenue = (totalRevenue != null) ? totalRevenue : BigDecimal.ZERO;
        this.totalOrders = totalOrders;
    }
}
