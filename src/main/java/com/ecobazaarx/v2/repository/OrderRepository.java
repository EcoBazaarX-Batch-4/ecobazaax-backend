package com.ecobazaarx.v2.repository;

import com.ecobazaarx.v2.dto.AdminStatsDto;
import com.ecobazaarx.v2.dto.SalesHistoryEntryDto;
import com.ecobazaarx.v2.dto.SellerStatsDto;
import com.ecobazaarx.v2.model.Order;
import com.ecobazaarx.v2.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {

    Page<Order> findByUserIdOrderByOrderDateDesc(Long userId, Pageable pageable);
    List<Order> findByUserId(Long userId);

    @Query("SELECT COUNT(o) > 0 FROM Order o JOIN o.orderItems oi " +
            "WHERE o.user.id = :userId AND oi.product.id = :productId AND o.status = 'DELIVERED'")
    boolean hasUserPurchasedProduct(
            @Param("userId") Long userId,
            @Param("productId") Long productId
    );

    @Query("SELECT new com.ecobazaarx.v2.dto.SellerStatsDto(" +
            "COALESCE(SUM(oi.pricePerItem * oi.quantity), 0.0), " +
            "COALESCE(SUM(oi.quantity), 0L), " +
            "COALESCE(COUNT(DISTINCT oi.order.id), 0L)) " +
            "FROM OrderItem oi " +
            "WHERE oi.product.seller.id = :sellerId " +
            "AND oi.order.status = 'DELIVERED'")
    SellerStatsDto getSellerSalesStats(@Param("sellerId") Long sellerId);

//    @Query("SELECT new com.ecobazaarx.v2.dto.SalesHistoryEntryDto(" +
//            "FUNCTION('DATE_FORMAT', oi.order.orderDate, '%Y-%m-%d'), " +
//            "COALESCE(SUM(oi.pricePerItem * oi.quantity), 0.0), " +
//            "COALESCE(COUNT(DISTINCT oi.order.id), 0L)) FROM OrderItem oi " + // <-- NO ASTERISK
//            "WHERE oi.product.seller.id = :sellerId " +
//            "AND oi.order.status = 'DELIVERED' " +
//            "AND oi.order.orderDate >= :startDate " +
//            "GROUP BY FUNCTION('DATE_FORMAT', oi.order.orderDate, '%Y-%m-%d') " +
//            "ORDER BY FUNCTION('DATE_FORMAT', oi.order.orderDate, '%Y-%m-%d') ASC")
//    List<SalesHistoryEntryDto> getSellerSalesHistory(
//            @Param("sellerId") Long sellerId,
//            @Param("startDate") LocalDateTime startDate
//    );

    @Query("SELECT new com.ecobazaarx.v2.dto.AdminStatsDto(" +
            "COALESCE(SUM(o.totalAmount), 0.0), " +
            "COALESCE(COUNT(o.id), 0L)) " +
            "FROM Order o " +
            "WHERE o.status = 'DELIVERED'")
    AdminStatsDto getAdminSalesStats();

    @Query("SELECT COALESCE(AVG(o.totalCarbonFootprint), 0.0) " +
            "FROM Order o " +
            "WHERE o.status = 'DELIVERED'")
    BigDecimal getSiteWideAverageCarbon();

    @Query("SELECT oi.product.seller " +
            "FROM OrderItem oi " +
            "WHERE oi.order.status = 'DELIVERED' " +
            "GROUP BY oi.product.seller " +
            "ORDER BY SUM(oi.pricePerItem * oi.quantity) DESC")
    List<User> findTopSellersByRevenue(Pageable pageable);

    @Query("SELECT DISTINCT oi.order " +
            "FROM OrderItem oi " +
            "WHERE oi.product.seller.id = :sellerId " +
            "AND oi.order.status = 'DELIVERED' " +
            "ORDER BY oi.order.orderDate DESC")
    List<Order> findAllDeliveredOrdersBySellerId(@Param("sellerId") Long sellerId);
}
