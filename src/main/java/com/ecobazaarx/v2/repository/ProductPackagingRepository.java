package com.ecobazaarx.v2.repository;

import com.ecobazaarx.v2.model.ProductPackaging;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductPackagingRepository extends JpaRepository<ProductPackaging, Long> {
}
