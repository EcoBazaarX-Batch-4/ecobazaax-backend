package com.ecobazaarx.v2.dto;

import lombok.Data;

@Data
public class CheckoutRequest {
    private String paymentMethodId;
    private int ecoPointsToRedeem;
}
