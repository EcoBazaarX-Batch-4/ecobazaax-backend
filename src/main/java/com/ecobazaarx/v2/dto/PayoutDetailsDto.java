package com.ecobazaarx.v2.dto;

import lombok.Data;

@Data
public class PayoutDetailsDto {
    private String bankName;
    private String accountHolderName;
    private String accountNumber;
    private String ifscCode;
}
