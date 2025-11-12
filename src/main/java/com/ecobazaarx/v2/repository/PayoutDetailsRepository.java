package com.ecobazaarx.v2.repository;

import com.ecobazaarx.v2.model.PayoutDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PayoutDetailsRepository extends JpaRepository<PayoutDetails, Long> {
}
