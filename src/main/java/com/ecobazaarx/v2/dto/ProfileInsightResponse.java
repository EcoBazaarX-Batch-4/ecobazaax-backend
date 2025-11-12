package com.ecobazaarx.v2.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class ProfileInsightResponse {

    private int totalOrders;
    private BigDecimal totalSpent;
    private BigDecimal lifetimeTotalCarbon;
    private BigDecimal personalAverageCarbon;
    private BigDecimal siteAverageCarbon;
}
