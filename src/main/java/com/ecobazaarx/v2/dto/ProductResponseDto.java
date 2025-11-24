package com.ecobazaarx.v2.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;


@Data
@Builder
public class ProductResponseDto {
    private Long id;
    private String name;
    private String description; // <-- ADDED
    private BigDecimal price;
    private String imageUrl;
    private Integer stockQuantity;
    private String categoryName;
    private Integer categoryId;     // <-- ADDED
    private String sellerStoreName;
    private BigDecimal cradleToWarehouseFootprint;
    private Integer ecoPoints;
    private boolean isArchived;
    private Integer transportZoneId; // <-- ADDED
}
