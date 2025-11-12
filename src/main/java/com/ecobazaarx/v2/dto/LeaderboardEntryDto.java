package com.ecobazaarx.v2.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class LeaderboardEntryDto {
    private int rank;
    private String userName;
    private int rankLevel;
    private BigDecimal averageCarbonFootprint;
}
