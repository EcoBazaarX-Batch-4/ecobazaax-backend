package com.ecobazaarx.v2.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductMaterialDto {
    private Integer materialId; // material id
    private BigDecimal weightKg; // material weight

    public ProductMaterialDto(Integer materialId, BigDecimal weightKg) {
        this.materialId = materialId;
        this.weightKg = weightKg;
    }
}
