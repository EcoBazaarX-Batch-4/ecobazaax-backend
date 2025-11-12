package com.ecobazaarx.v2.repository;

import com.ecobazaarx.v2.model.OrderItem;
import com.ecobazaarx.v2.model.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    @Query("SELECT oi.product " +
            "FROM OrderItem oi " +
            "WHERE oi.product.seller.id = :sellerId " +
            "AND oi.order.status = 'DELIVERED' " +
            "GROUP BY oi.product " +
            "ORDER BY SUM(oi.quantity) DESC")
    List<Product> findTopSellingProductsBySeller(
            @Param("sellerId") Long sellerId,
            Pageable pageable
    );

    @Query("SELECT oi.product " +
            "FROM OrderItem oi " +
            "WHERE oi.order.status = 'DELIVERED' " +
            "GROUP BY oi.product " +
            "ORDER BY SUM(oi.quantity) DESC")
    List<Product> findTopSellingProductsGlobal(Pageable pageable);
}