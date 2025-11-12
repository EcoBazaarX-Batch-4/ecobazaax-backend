package com.ecobazaarx.v2.repository;

import com.ecobazaarx.v2.model.ManufacturingProcess;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ManufacturingProcessRepository extends JpaRepository<ManufacturingProcess, Integer> {
}
