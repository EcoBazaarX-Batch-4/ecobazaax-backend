package com.ecobazaarx.v2.config;

import com.ecobazaarx.v2.model.Role;
import com.ecobazaarx.v2.model.RoleName;
import com.ecobazaarx.v2.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {
        if (roleRepository.count() == 0) {
            Role customerRole = new Role(RoleName.ROLE_CUSTOMER);
            Role sellerRole = new Role(RoleName.ROLE_SELLER);
            Role adminRole = new Role(RoleName.ROLE_ADMIN);

            roleRepository.saveAll(Arrays.asList(customerRole, sellerRole, adminRole));
            System.out.println("Initialized CUSTOMER, SELLER, and ADMIN roles in the database.");
        }
    }
}
