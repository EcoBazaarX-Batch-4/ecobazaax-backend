package com.ecobazaarx.v2.repository;

import com.ecobazaarx.v2.model.ProductManufacturing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductManufacturingRepository extends JpaRepository<ProductManufacturing, Long> {
}
