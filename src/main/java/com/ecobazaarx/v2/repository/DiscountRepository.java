package com.ecobazaarx.v2.repository;

import com.ecobazaarx.v2.model.Discount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DiscountRepository extends JpaRepository<Discount, Long> {

    Optional<Discount> findByCode(String code);

    @Query("SELECT d FROM Discount d WHERE d.validFrom <= :now AND d.validUntil >= :now AND d.usageLimit > 0")
    List<Discount> findActiveDiscounts(LocalDateTime now);
}
