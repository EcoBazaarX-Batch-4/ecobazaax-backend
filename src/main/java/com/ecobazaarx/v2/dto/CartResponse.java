package com.ecobazaarx.v2.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class CartResponse {
    private Long cartId;
    private List<CartItemResponse> items;
    private BigDecimal productsTotalAmount;
    private BigDecimal productsTotalCarbon;
    private DiscountResponse appliedDiscount;
    private BigDecimal grandTotal;
    private BigDecimal shippingCost;
    private BigDecimal shippingCarbon;
    private BigDecimal taxAmount;
    private AddressDto shippingAddress;
}
