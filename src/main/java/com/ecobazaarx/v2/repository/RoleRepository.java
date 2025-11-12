package com.ecobazaarx.v2.repository;

import com.ecobazaarx.v2.model.Role;
import com.ecobazaarx.v2.model.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByName(RoleName name);
}
