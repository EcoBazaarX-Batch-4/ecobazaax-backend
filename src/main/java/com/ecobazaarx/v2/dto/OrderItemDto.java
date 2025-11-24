package com.ecobazaarx.v2.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class OrderItemDto {
    private Long productId;
    private String productName;
    private String imageUrl;
    private int quantity;
    private BigDecimal pricePerItem;
    private BigDecimal carbonFootprintPerItem;
}
