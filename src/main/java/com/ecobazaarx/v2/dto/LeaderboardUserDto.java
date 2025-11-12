package com.ecobazaarx.v2.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class LeaderboardUserDto {
    private String name;
    private String email;
    private int valueInt; // Total Orders
    private BigDecimal valueDecimal; // Avg Carbon or Total Revenue
}
