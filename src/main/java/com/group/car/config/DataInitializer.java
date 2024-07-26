package com.group.car.config;

import com.group.car.models.Role;
import com.group.car.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Autowired
    private RoleRepository roleRepository;

    @Bean
    public CommandLineRunner initData() {
        return args -> {

            // Create "CarOwner" role
            if (roleRepository.findByName("CarOwner") == null) {
                Role carOwnerRole = new Role();
                carOwnerRole.setName("CarOwner");
                roleRepository.save(carOwnerRole);
            }


            // Create "Customer" role
            if (roleRepository.findByName("Customer") == null) {
                Role customerRole = new Role();
                customerRole.setName("Customer");
                roleRepository.save(customerRole);
            }
        };
    }
}
