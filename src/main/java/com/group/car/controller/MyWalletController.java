package com.group.car.controller;

import com.group.car.models.Account;
import com.group.car.models.CarOwner;
import com.group.car.models.Customer;
import com.group.car.models.Role;
import com.group.car.repository.AccountRepository;
import com.group.car.repository.CarOwnerRepository;
import com.group.car.repository.CustomerRepository;
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

    @GetMapping("/my-wallet")
    public String tomyWallet(Model model, Principal principal){
        String email = principal.getName();
        Account emailAccount = accountRepository.findByEmail(email);

        if (emailAccount == null){
            return "redirect:/login";
        }

        if (emailAccount.getRoles().stream().anyMatch(role -> role.getName().equals("Customer"))) {
            Customer customer = customerRepository.findByAccountId(emailAccount.getId());
            model.addAttribute("account", emailAccount);
            model.addAttribute("customer", customer);
            model.addAttribute("carOwner", null);
        } else if (emailAccount.getRoles().stream().anyMatch(role -> role.getName().equals("CarOwner"))) {
            CarOwner carOwner = carOwnerRepository.findByAccountId(emailAccount.getId());
            model.addAttribute("account", emailAccount);
            model.addAttribute("carOwner", carOwner);
            model.addAttribute("customer", null);
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
    public ResponseEntity<String> withdraw(@RequestParam String amount, Principal principal) {
        String email = principal.getName();
        Account account = accountRepository.findByEmail(email);

        if (account != null) {
            int currentBalance = 0;
            boolean isCustomer = false;

            if (account.getCustomer() != null) {
                currentBalance = account.getCustomer().getWallet();
                isCustomer = true;
            } else if (account.getCarOwner() != null) {
                currentBalance = account.getCarOwner().getWallet();
            }

            try {
                int amountToWithdraw = Integer.parseInt(amount.replace(",", "").replaceAll("[^0-9]", ""));
                System.out.println("Current Balance: " + currentBalance);
                System.out.println("Amount to Withdraw: " + amountToWithdraw);

                if (amountToWithdraw <= currentBalance) {
                    int newBalance = currentBalance - amountToWithdraw;

                    if (isCustomer) {
                        Customer customer = account.getCustomer();
                        customer.setWallet(newBalance);
                        customerRepository.save(customer);
                    } else {
                        CarOwner carOwner = account.getCarOwner();
                        carOwner.setWallet(newBalance);
                        carOwnerRepository.save(carOwner);
                    }
                    return ResponseEntity.ok("Withdraw successful");
                } else {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Insufficient balance");
                }
            } catch (NumberFormatException e) {
                System.err.println("Invalid amount format: " + amount);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid amount format");
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account not found");
    }


    @PostMapping("/topup")
    public ResponseEntity<String> topUp(@RequestParam String amount, Principal principal) {
        String email = principal.getName();
        Account account = accountRepository.findByEmail(email);

        if (account != null) {
            int currentBalance = 0;
            boolean isCustomer = false;

            if (account.getCustomer() != null) {
                currentBalance = account.getCustomer().getWallet();
                isCustomer = true;
            } else if (account.getCarOwner() != null) {
                currentBalance = account.getCarOwner().getWallet();
            }

            try {
                int amountToTopUp = Integer.parseInt(amount.replace(",", "").replaceAll("[^0-9]", ""));
                System.out.println("Current Balance: " + currentBalance);
                System.out.println("Amount to Top-Up: " + amountToTopUp);

                int newBalance = currentBalance + amountToTopUp;

                if (isCustomer) {
                    Customer customer = account.getCustomer();
                    customer.setWallet(newBalance);
                    customerRepository.save(customer);
                } else {
                    CarOwner carOwner = account.getCarOwner();
                    carOwner.setWallet(newBalance);
                    carOwnerRepository.save(carOwner);
                }
                return ResponseEntity.ok("Top-up successful");
            } catch (NumberFormatException e) {
                System.err.println("Invalid amount format: " + amount);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid amount format");
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account not found");
    }

    //Them search Date from - to
}
