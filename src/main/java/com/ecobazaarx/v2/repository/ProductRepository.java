package com.ecobazaarx.v2.repository;

import com.ecobazaarx.v2.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    Page<Product> findBySellerId(Long sellerId, Pageable pageable);

    @Query("SELECT COALESCE(AVG(p.cradleToWarehouseFootprint), 0.0) FROM Product p WHERE p.seller.id = :sellerId")
    BigDecimal getSellerAverageProductCarbon(@Param("sellerId") Long sellerId);

    List<Product> findBySellerIdOrderByStockQuantityAsc(Long sellerId, Pageable pageable);
    List<Product> findBySellerIdOrderByCradleToWarehouseFootprintAsc(Long sellerId, Pageable pageable);
    List<Product> findBySellerIdOrderByCradleToWarehouseFootprintDesc(Long sellerId, Pageable pageable);
    List<Product> findByOrderByCreatedAtDesc(Pageable pageable);
    List<Product> findByOrderByAverageRatingDesc(Pageable pageable);
    List<Product> findByOrderByCradleToWarehouseFootprintAsc(Pageable pageable);
    List<Product> findByOrderByEcoPointsDesc(Pageable pageable);
    List<Product> findByCategoryIdAndIdNot(Integer categoryId, Long productId, Pageable pageable);
}
