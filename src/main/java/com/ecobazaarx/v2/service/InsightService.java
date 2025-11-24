package com.ecobazaarx.v2.service;

import com.ecobazaarx.v2.dto.*;
import com.ecobazaarx.v2.model.Order;
import com.ecobazaarx.v2.model.Product;
import com.ecobazaarx.v2.model.RoleName;
import com.ecobazaarx.v2.model.User;
import com.ecobazaarx.v2.repository.OrderItemRepository;
import com.ecobazaarx.v2.repository.OrderRepository;
import com.ecobazaarx.v2.repository.ProductRepository;
import com.ecobazaarx.v2.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class InsightService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductService productService;
    private final CsvExportService csvExportService;

    @Transactional(readOnly = true)
    public ProfileInsightResponse getProfileInsights(UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        List<Order> userOrders = orderRepository.findByUserId(user.getId());

        int totalOrders = userOrders.size();
        BigDecimal totalSpent = BigDecimal.ZERO;
        BigDecimal lifetimeTotalCarbon = BigDecimal.ZERO;
        BigDecimal personalAverageCarbon = BigDecimal.ZERO;

        if (!userOrders.isEmpty()) {
            totalSpent = userOrders.stream()
                    .map(Order::getTotalAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            lifetimeTotalCarbon = userOrders.stream()
                    .map(Order::getTotalCarbonFootprint)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            personalAverageCarbon = lifetimeTotalCarbon.divide(
                    new BigDecimal(totalOrders), 2, RoundingMode.HALF_UP
            );
        }

        List<Order> allOrders = orderRepository.findAll();
        BigDecimal siteAverageCarbon = BigDecimal.ZERO;

        if (!allOrders.isEmpty()) {
            BigDecimal siteTotalCarbon = allOrders.stream()
                    .map(Order::getTotalCarbonFootprint)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            siteAverageCarbon = siteTotalCarbon.divide(
                    new BigDecimal(allOrders.size()), 2, RoundingMode.HALF_UP
            );
        }

        return ProfileInsightResponse.builder()
                .totalOrders(totalOrders)
                .totalSpent(totalSpent)
                .lifetimeTotalCarbon(lifetimeTotalCarbon)
                .personalAverageCarbon(personalAverageCarbon)
                .siteAverageCarbon(siteAverageCarbon)
                .build();
    }

    @Transactional(readOnly = true)
    public SellerInsightResponse getSellerInsights(UserDetails userDetails) {
        User seller = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("Seller not found"));

        SellerStatsDto salesStats = orderRepository.getSellerSalesStats(seller.getId());

        BigDecimal avgCarbon = productRepository.getSellerAverageProductCarbon(seller.getId());
        if (avgCarbon == null) {
            avgCarbon = BigDecimal.ZERO;
        }

        long inventoryCount = productRepository.count((root, query, cb) ->
                cb.and(
                        cb.equal(root.get("seller").get("id"), seller.getId()),
                        cb.isFalse(root.get("isArchived"))
                )
        );

        return SellerInsightResponse.builder()
                .totalRevenue(salesStats.totalRevenue)
                .totalProductsSold(salesStats.totalProductsSold)
                .totalOrders(salesStats.totalOrders)
                .averageProductCarbon(avgCarbon.setScale(2, RoundingMode.HALF_UP))
                .totalInventory(inventoryCount)
                .build();
    }

//    @Transactional(readOnly = true)
//    public List<SalesHistoryEntryDto> getSellerSalesHistory(UserDetails userDetails, int rangeInDays) {
//        User seller = userRepository.findByEmail(userDetails.getUsername())
//                .orElseThrow(() -> new EntityNotFoundException("Seller not found"));
//
//        LocalDateTime startDate = LocalDateTime.now().minus(rangeInDays, ChronoUnit.DAYS);
//
//        return orderRepository.getSellerSalesHistory(seller.getId(), startDate);
//    }

    @Transactional(readOnly = true)
    public SellerProductPerformanceDto getProductPerformance(UserDetails userDetails) {
        User seller = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("Seller not found"));

        Pageable top5 = PageRequest.of(0, 5);
        List<Product> topSellingProducts = orderItemRepository.findTopSellingProductsBySeller(seller.getId(), top5);
        List<Product> lowestStockProducts = productRepository.findBySellerIdOrderByStockQuantityAsc(seller.getId(), top5);
        List<Product> lowestCarbonProducts = productRepository.findBySellerIdOrderByCradleToWarehouseFootprintAsc(seller.getId(), top5);
        List<Product> highestCarbonProducts = productRepository.findBySellerIdOrderByCradleToWarehouseFootprintDesc(seller.getId(), top5);
        return SellerProductPerformanceDto.builder()
                .topSelling(mapProductListToDto(topSellingProducts))
                .lowestStock(mapProductListToDto(lowestStockProducts))
                .lowestCarbon(mapProductListToDto(lowestCarbonProducts))
                .highestCarbon(mapProductListToDto(highestCarbonProducts))
                .build();
    }

    @Transactional(readOnly = true)
    public AdminInsightResponse getAdminInsights() {

        AdminStatsDto salesStats = orderRepository.getAdminSalesStats();

        long totalCustomers = userRepository.countByRoles_Name(RoleName.ROLE_CUSTOMER);
        long totalSellers = userRepository.countByRoles_Name(RoleName.ROLE_SELLER);

        long totalProducts = productRepository.count();

        BigDecimal avgCarbon = orderRepository.getSiteWideAverageCarbon();

        return AdminInsightResponse.builder()
                .totalCustomers(totalCustomers)
                .totalSellers(totalSellers)
                .totalProducts(totalProducts)
                .totalPlatformRevenue(salesStats.totalRevenue)
                .totalOrders(salesStats.totalOrders)
                .siteWideAverageCarbon(avgCarbon.setScale(2, RoundingMode.HALF_UP))
                .build();
    }

    @Transactional(readOnly = true)
    public AdminLeaderboardDto getAdminLeaderboards() {
        Pageable top10 = PageRequest.of(0, 10);

        List<User> topCustomers = userRepository.findTopCustomersByOrders(top10);

        List<User> greenestCustomers = userRepository.findGreenestCustomers(top10);

        List<User> topSellers = orderRepository.findTopSellersByRevenue(top10);

        List<LeaderboardUserDto> topSellersWithRevenue = topSellers.stream()
                .map(seller -> {
                    SellerStatsDto stats = orderRepository.getSellerSalesStats(seller.getId());
                    return mapToLeaderboardDto(seller, 0, null, stats.totalRevenue);
                })
                .collect(Collectors.toList());

        return AdminLeaderboardDto.builder()
                .topCustomersByOrders(mapToLeaderboardDtoList(topCustomers, true, false))
                .greenestCustomers(mapToLeaderboardDtoList(greenestCustomers, false, true))
                .topSellersByRevenue(topSellersWithRevenue)
                .build();
    }

    @Transactional(readOnly = true)
    public String getSellerSalesCsv(UserDetails userDetails) {
        User seller = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("Seller not found"));

        List<Order> orders = orderRepository.findAllDeliveredOrdersBySellerId(seller.getId());

        return csvExportService.writeOrdersToCsv(orders);
    }

    @Transactional(readOnly = true)
    public String getAdminSalesCsv() {

        List<Order> allOrders = orderRepository.findAll();

        return csvExportService.writeOrdersToCsv(allOrders);
    }

    private List<LeaderboardUserDto> mapToLeaderboardDtoList(List<User> users, boolean useOrderCount, boolean useCarbon) {
        return IntStream.range(0, users.size())
                .mapToObj(i -> {
                    User user = users.get(i);
                    return mapToLeaderboardDto(
                            user,
                            useOrderCount ? user.getTotalOrderCount() : 0,
                            useCarbon ? user.getLifetimeAverageCarbon() : null,
                            null
                    );
                })
                .collect(Collectors.toList());
    }

    private LeaderboardUserDto mapToLeaderboardDto(User user, int orderCount, BigDecimal avgCarbon, BigDecimal totalRevenue) {
        return LeaderboardUserDto.builder()
                .name(user.getName())
                .email(user.getEmail())
                .valueInt(orderCount)
                .valueDecimal(avgCarbon != null ? avgCarbon.setScale(2, RoundingMode.HALF_UP) :
                        totalRevenue != null ? totalRevenue.setScale(2, RoundingMode.HALF_UP) : null)
                .build();
    }

    private List<ProductResponseDto> mapProductListToDto(List<Product> products) {
        return products.stream()
                .map(productService::mapToResponseDto)
                .collect(Collectors.toList());
    }
}
