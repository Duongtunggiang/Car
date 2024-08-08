package com.group.car.repository;

import com.group.car.models.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Customer findByAccountId(Long accountId);
    Optional<Customer> findByEmail(String email);
//    Customer findByEmailCustomer(String email);
}