package com.ecobazaarx.v2.dto;

import com.ecobazaarx.v2.model.RoleName;
import com.ecobazaarx.v2.model.SellerApplicationStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Set;

@Data
@Builder
public class AdminUserResponseDto {
    private Long id;
    private String name;
    private String email;
    private Set<RoleName> roles;
    private SellerApplicationStatus sellerStatus;
    private String storeName;
    private boolean isAccountNonLocked;
    private boolean isEnabled;
    private int ecoPoints;
    private int rankLevel;
    private BigDecimal lifetimeAverageCarbon;
}
