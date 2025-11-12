package com.ecobazaarx.v2.repository;

import com.ecobazaarx.v2.model.EcoPointLedger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EcoPointLedgerRepository extends JpaRepository<EcoPointLedger, Long> {

    Page<EcoPointLedger> findByUserIdOrderByTransactionDateDesc(Long userId, Pageable pageable);
}
