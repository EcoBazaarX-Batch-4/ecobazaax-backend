package com.ecobazaarx.v2.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class SellerProductPerformanceDto {
    private List<ProductResponseDto> topSelling;
    private List<ProductResponseDto> lowestStock;
    private List<ProductResponseDto> lowestCarbon;
    private List<ProductResponseDto> highestCarbon;
}
