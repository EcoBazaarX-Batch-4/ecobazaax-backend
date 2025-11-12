package com.ecobazaarx.v2.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class ShippingOptionDto {
    private Integer transportZoneId;
    private String name;
    private BigDecimal cost;
    private BigDecimal carbonFootprint;
}
