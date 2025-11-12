package com.ecobazaarx.v2.dto;

import com.ecobazaarx.v2.model.RoleName;
import lombok.Data;
import java.util.Set;

@Data
public class AdminUserUpdateRequest {
    private String name;
    private String email;
    private Set<RoleName> roles;
    private boolean isAccountNonLocked;
    private boolean isEnabled;
}
