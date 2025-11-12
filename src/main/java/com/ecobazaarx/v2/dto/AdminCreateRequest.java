package com.ecobazaarx.v2.dto;

import lombok.Data;

@Data
public class AdminCreateRequest {
    private String name;
    private String email;
    private String password;
}
