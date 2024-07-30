package com.group.car.controller;

import com.group.car.models.Account;
import com.group.car.models.CarOwner;
import com.group.car.models.Customer;
import com.group.car.repository.AccountRepository;
import com.group.car.repository.CarOwnerRepository;
import com.group.car.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequestMapping("/mywallet")
public class MyWalletController {
    @Autowired
    AccountRepository accountRepository;

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    CarOwnerRepository carOwnerRepository;

    //My wallet
//    @GetMapping("/my-wallet")
//    public String myWallet(){
//        return "wallet/my-wallet";
//    }

    @GetMapping("/my-wallet")
    public String tomyWallet(Model model,
                             Principal principal){
        String email = principal.getName();
        Account emailAccount = accountRepository.findByEmail(email);

        if (emailAccount == null){
            return "redirect:/login";
        }

        if (emailAccount.getRoles().stream().anyMatch(role -> role.getName().equals("Customer"))) {
            Customer customer = customerRepository.findByAccountId(emailAccount.getId());
            model.addAttribute("account", emailAccount);
            model.addAttribute("customer", customer);
            model.addAttribute("carOwner", null);  // Explicitly set carOwner to null
        } else if (emailAccount.getRoles().stream().anyMatch(role -> role.getName().equals("CarOwner"))) {
            CarOwner carOwner = carOwnerRepository.findByAccountId(emailAccount.getId());
            model.addAttribute("account", emailAccount);
            model.addAttribute("carOwner", carOwner);
            model.addAttribute("customer", null);  // Explicitly set customer to null
        }

        return "wallet/my-wallet";
    }
}
