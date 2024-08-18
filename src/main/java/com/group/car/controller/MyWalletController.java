package com.group.car.controller;

import com.group.car.models.*;
import com.group.car.repository.AccountRepository;
import com.group.car.repository.CarOwnerRepository;
import com.group.car.repository.CustomerRepository;
import com.group.car.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/mywallet")
public class MyWalletController {
    @Autowired
    AccountRepository accountRepository;

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    CarOwnerRepository carOwnerRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @GetMapping("/my-wallet")
    public String tomyWallet(Model model, Principal principal) {
        String email = principal.getName();
        Account emailAccount = accountRepository.findByEmail(email);

        if (emailAccount == null) {
            return "redirect:/login";
        }

        if (emailAccount.getRoles().stream().anyMatch(role -> role.getName().equals("Customer"))) {
            Customer customer = customerRepository.findByAccountId(emailAccount.getId());
            List<Transaction> transactions = transactionRepository.findByCustomerOrderByTransactionDateTimeDesc(customer);
            model.addAttribute("account", emailAccount);
            model.addAttribute("customer", customer);
            model.addAttribute("carOwner", null);
            model.addAttribute("transactions", transactions);
        } else if (emailAccount.getRoles().stream().anyMatch(role -> role.getName().equals("CarOwner"))) {
            CarOwner carOwner = carOwnerRepository.findByAccountId(emailAccount.getId());
            List<Transaction> transactions = transactionRepository.findByCarOwnerOrderByTransactionDateTimeDesc(carOwner);
            model.addAttribute("account", emailAccount);
            model.addAttribute("carOwner", carOwner);
            model.addAttribute("customer", null);
            model.addAttribute("transactions", transactions);
        }
        setUpUserRole(model);
        model.addAttribute("currentPage", "wallet");
        return "wallet/my-wallet";
    }

    private void setUpUserRole(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String username = userDetails.getUsername();

            // Tìm tài khoản và vai trò của người dùng dựa trên username hoặc email
            Account account = accountRepository.findByEmail(username);
            if (account != null) {
                Set<Role> roles = account.getRoles();
                model.addAttribute("userRoles", roles);
                if (roles.stream().anyMatch(role -> role.getName().equals("CarOwner"))) {
                    model.addAttribute("userRole", "CarOwner");
                } else if (roles.stream().anyMatch(role -> role.getName().equals("Customer"))) {
                    model.addAttribute("userRole", "Customer");
                }
            } else {
                model.addAttribute("userRole", "Guest");
            }
        } else {
            model.addAttribute("userRole", "Guest");
        }
    }


    @PostMapping("/withdraw")
    public String withdrawWallet(@RequestParam("amount") int amount, Principal principal, Model model) {
        String email = principal.getName();
        Account emailAccount = accountRepository.findByEmail(email);

        if (emailAccount == null) {
            model.addAttribute("errorMessage", "User not found. Please login again.");
            return "wallet/my-wallet";
        }

        if (emailAccount.getRoles().stream().anyMatch(role -> role.getName().equals("Customer"))) {
            Customer customer = customerRepository.findByAccountId(emailAccount.getId());
            if (customer.getWallet() >= amount) {
                customer.setWallet(customer.getWallet() - amount);
                customerRepository.save(customer);

                // Lưu giao dịch
                Transaction transaction = new Transaction();
                transaction.setAmount(amount);
                transaction.setType("Withdraw");
                transaction.setCustomer(customer);
                transaction.setTransactionDateTime(new Date());
                transaction.setWalletBalance(customer.getWallet()); // Cập nhật số dư
                transactionRepository.save(transaction);

                model.addAttribute("successMessage", "Withdrawal successful!");
            } else {
                model.addAttribute("errorMessage", "Insufficient wallet balance.");
            }
        } else if (emailAccount.getRoles().stream().anyMatch(role -> role.getName().equals("CarOwner"))) {
            CarOwner carOwner = carOwnerRepository.findByAccountId(emailAccount.getId());
            if (carOwner.getWallet() >= amount) {
                carOwner.setWallet(carOwner.getWallet() - amount);
                carOwnerRepository.save(carOwner);

                // Lưu giao dịch
                Transaction transaction = new Transaction();
                transaction.setAmount(amount);
                transaction.setType("Withdraw");
                transaction.setCarOwner(carOwner);
                transaction.setTransactionDateTime(new Date());
                transaction.setWalletBalance(carOwner.getWallet()); // Cập nhật số dư
                transactionRepository.save(transaction);

                model.addAttribute("successMessage", "Withdrawal successful!");
            } else {
                model.addAttribute("errorMessage", "Insufficient wallet balance.");
            }
        }

        return "wallet/my-wallet";
    }

    @PostMapping("/topup")
    public String topUpWallet(@RequestParam("amount") int amount, Principal principal, Model model) {
        String email = principal.getName();
        Account emailAccount = accountRepository.findByEmail(email);

        if (emailAccount == null) {
            model.addAttribute("errorMessage", "User not found. Please login again.");
            return "wallet/my-wallet";
        }

        if (amount <= 0) {
            model.addAttribute("errorMessage", "Invalid amount. Please enter a positive value.");
            return "wallet/my-wallet";
        }

        if (emailAccount.getRoles().stream().anyMatch(role -> role.getName().equals("Customer"))) {
            Customer customer = customerRepository.findByAccountId(emailAccount.getId());
            customer.setWallet(customer.getWallet() + amount);
            customerRepository.save(customer);

            // Lưu giao dịch
            Transaction transaction = new Transaction();
            transaction.setAmount(amount);
            transaction.setType("Top-Up");
            transaction.setCustomer(customer);
            transaction.setTransactionDateTime(new Date());
            transaction.setWalletBalance(customer.getWallet()); // Cập nhật số dư
            transactionRepository.save(transaction);

            model.addAttribute("successMessage", "Top-Up successful!");
        } else if (emailAccount.getRoles().stream().anyMatch(role -> role.getName().equals("CarOwner"))) {
            CarOwner carOwner = carOwnerRepository.findByAccountId(emailAccount.getId());
            carOwner.setWallet(carOwner.getWallet() + amount);
            carOwnerRepository.save(carOwner);

            // Lưu giao dịch
            Transaction transaction = new Transaction();
            transaction.setAmount(amount);
            transaction.setType("Top-Up");
            transaction.setCarOwner(carOwner);
            transaction.setTransactionDateTime(new Date());
            transaction.setWalletBalance(carOwner.getWallet()); // Cập nhật số dư
            transactionRepository.save(transaction);

            model.addAttribute("successMessage", "Top-Up successful!");
        }

        return "wallet/my-wallet";
    }

    //Them search Date from - to
}
