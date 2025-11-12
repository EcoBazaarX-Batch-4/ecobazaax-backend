package com.ecobazaarx.v2.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductManufacturingDto {
    private Integer processId;
    private BigDecimal weightKg;
}
