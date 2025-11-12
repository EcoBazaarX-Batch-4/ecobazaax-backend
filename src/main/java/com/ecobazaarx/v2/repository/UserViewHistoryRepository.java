package com.ecobazaarx.v2.repository;

import com.ecobazaarx.v2.model.Product;
import com.ecobazaarx.v2.model.UserViewHistory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserViewHistoryRepository extends JpaRepository<UserViewHistory, Long> {

    Optional<UserViewHistory> findFirstByUserIdAndProductIdOrderByViewedAtDesc(Long userId, Long productId);

    @Query("SELECT h.product FROM UserViewHistory h " +
            "WHERE h.user.id = :userId " +
            "GROUP BY h.product " +
            "ORDER BY MAX(h.viewedAt) DESC")
    List<Product> findRecentlyViewedProductsByUserId(@Param("userId") Long userId, Pageable pageable);
}
