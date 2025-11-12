package com.ecobazaarx.v2.dto;

import com.ecobazaarx.v2.model.OrderStatus;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class OrderDto {
    private Long id;
    private LocalDateTime orderDate;
    private OrderStatus status;
    private BigDecimal totalAmount;
    private BigDecimal totalCarbonFootprint;
    private List<OrderItemDto> orderItems;
}
