package com.ecobazaarx.v2.dto;

import com.ecobazaarx.v2.model.OrderStatus;
import lombok.Data;

@Data
public class AdminOrderStatusRequest {
    private OrderStatus status;
}
