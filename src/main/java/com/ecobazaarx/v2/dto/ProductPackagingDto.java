package com.ecobazaarx.v2.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductPackagingDto {
    private Integer packagingMaterialId; // Packaging material id
    private BigDecimal weightKg; // Packaging material weight
}
