package com.ecobazaarx.v2.repository;

import com.ecobazaarx.v2.model.PackagingMaterial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PackagingMaterialRepository extends JpaRepository<PackagingMaterial, Integer> {
}
