package com.group.car.service;

import com.group.car.models.CarOwner;
import com.group.car.models.Customer;
import com.group.car.models.Transaction;
import com.group.car.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

import java.util.List;

@Service
public class WalletService {

    @Autowired
    private TransactionRepository transactionRepository;

    public List<Transaction> getTransactionsForCustomer(Customer customer) {
        return transactionRepository.findByCustomer(customer);
    }

    public List<Transaction> getTransactionsForCarOwner(CarOwner carOwner) {
        return transactionRepository.findByCarOwner(carOwner);
    }

    public void saveTransaction(Transaction transaction) {
        transactionRepository.save(transaction);
    }
}

