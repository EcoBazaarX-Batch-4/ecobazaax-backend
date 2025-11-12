package com.ecobazaarx.v2.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CheckoutResponse {
    private String clientSecret;
    private OrderDto order;
}
