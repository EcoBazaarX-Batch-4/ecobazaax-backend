package com.ecobazaarx.v2.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class EcoPointLedgerDto {
    private Long id;
    private int pointsChanged;
    private String reason;
    private LocalDateTime transactionDate;
}
