package com.ecobazaarx.v2.dto;

import lombok.Data;

@Data
public class AddressDto {
    private Long id;
    private String street;
    private String city;
    private String state;
    private String postalCode;
    private String country;
    private String label;
    private boolean isDefault;
}
