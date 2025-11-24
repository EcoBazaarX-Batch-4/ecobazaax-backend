package com.ecobazaarx.v2.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductPackagingDto {
    private Integer packagingMaterialId; // Packaging material id
    private BigDecimal weightKg; // Packaging material weight

    public ProductPackagingDto(Integer packagingMaterialId, BigDecimal weightKg) {
        this.packagingMaterialId = packagingMaterialId;
        this.weightKg = weightKg;
    }
}
