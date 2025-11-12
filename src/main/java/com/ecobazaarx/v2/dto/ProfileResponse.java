package com.ecobazaarx.v2.dto;

import com.ecobazaarx.v2.model.RoleName;
import com.ecobazaarx.v2.model.SellerApplicationStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Set;

@Data
@Builder
public class ProfileResponse {

    private Long id;
    private String name;
    private String email;
    private String referralCode;
    private Set<RoleName> roles;
    private Integer ecoPoints;
    private Integer rankLevel;
    private BigDecimal lifetimeAverageCarbon;
    private SellerApplicationStatus sellerStatus;
}
