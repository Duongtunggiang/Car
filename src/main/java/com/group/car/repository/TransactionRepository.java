package com.group.car.repository;

import com.group.car.models.CarOwner;
import com.group.car.models.Customer;
import com.group.car.models.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByCustomer(Customer customer);
    List<Transaction> findByCarOwner(CarOwner carOwner);
    List<Transaction> findByCustomerOrderByTransactionDateTimeDesc(Customer customer);
    List<Transaction> findByCarOwnerOrderByTransactionDateTimeDesc(CarOwner carOwner);
}
