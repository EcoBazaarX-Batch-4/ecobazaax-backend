package com.ecobazaarx.v2.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class AdminLeaderboardDto {
    private List<LeaderboardUserDto> topCustomersByOrders;
    private List<LeaderboardUserDto> greenestCustomers;
    private List<LeaderboardUserDto> topSellersByRevenue;
}
