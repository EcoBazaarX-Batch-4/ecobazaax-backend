package com.ecobazaarx.v2.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class HomepageRecommendationDto {
    private List<ProductResponseDto> topSellers;
    private List<ProductResponseDto> newArrivals;
    private List<ProductResponseDto> topRated;
    private List<ProductResponseDto> topLowestCarbon;
    private List<ProductResponseDto> topEcoRewards;
}
