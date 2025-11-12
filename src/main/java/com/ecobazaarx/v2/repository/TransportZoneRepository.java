package com.ecobazaarx.v2.repository;

import com.ecobazaarx.v2.model.TransportZone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TransportZoneRepository extends JpaRepository<TransportZone, Integer> {
    Optional<TransportZone> findByName(String name);
}
