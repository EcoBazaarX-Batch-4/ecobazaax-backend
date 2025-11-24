package com.ecobazaarx.v2.repository;

import com.ecobazaarx.v2.model.RoleName;
import com.ecobazaarx.v2.model.SellerApplicationStatus;
import com.ecobazaarx.v2.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    // used by the system
    Optional<User> findByEmail(String email);
    // used by the system
    Boolean existsByEmail(String email);
    // used by the admin
    List<User> findBySellerStatus(SellerApplicationStatus status);
    Page<User> findByRankLevel(int rankLevel, Pageable pageable);
    long countByRoles_Name(RoleName roleName);

    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = 'ROLE_CUSTOMER' ORDER BY u.totalOrderCount DESC")
    List<User> findTopCustomersByOrders(Pageable pageable);

    @Query("SELECT u FROM User u JOIN u.roles r " +
            "WHERE r.name = 'ROLE_CUSTOMER' AND u.totalOrderCount > 0 " +
            "ORDER BY u.lifetimeAverageCarbon ASC, u.rankLevelAchievedAt ASC")
    List<User> findGreenestCustomers(Pageable pageable);

//    @Query("SELECT u FROM User u " +
//            "WHERE u.roles.size = 1 " +
//            "AND EXISTS (SELECT r FROM u.roles r WHERE r.name = 'ROLE_CUSTOMER')")
//    Page<User> findPureCustomers(Pageable pageable);

    boolean existsByReferralCode(String referralCode);
    Optional<User> findByReferralCode(String referralCode);
}
