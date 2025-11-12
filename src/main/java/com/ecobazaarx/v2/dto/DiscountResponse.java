package com.ecobazaarx.v2.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class DiscountResponse {
    private String code;
    private BigDecimal amountSaved;
}
