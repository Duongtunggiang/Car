package com.group.car.controller;

import com.group.car.models.Account;
import com.group.car.models.RegisterDto;
import com.group.car.models.Role;
import com.group.car.repository.AccountRepository;
import com.group.car.repository.RoleRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@Controller
public class AccountController {
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/register")
    public String register(Model model){
        RegisterDto registerDto = new RegisterDto();
        model.addAttribute(registerDto);
        model.addAttribute("success",false);
        return "register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute RegisterDto registerDto, BindingResult result, Model model){
        if (!registerDto.getPassword().equals(registerDto.getConfirmPassword())){
            result.addError(
                    new FieldError("registerDto","confirmPassword",
                            "Password and Confirm password do not match")
            );
        }
        Account account = accountRepository.findByEmail(registerDto.getEmail());
        if (account!=null){
            result.addError(
                    new FieldError("registerDto","email",
                            "Email address is already used")
            );
        }
        if (result.hasErrors()){
            return "register";
        }
        try{
            var bCryptEncoder = new BCryptPasswordEncoder();

            Account newAccount = new Account();
            newAccount.setUsername(registerDto.getUsername());
            newAccount.setEmail(registerDto.getEmail());
            newAccount.setPassword(bCryptEncoder.encode(registerDto.getPassword()));
            newAccount.setEnabled(true);

            // Xét role mặc định là Customer
            Role customerRole = roleRepository.findByName("Customer");
            newAccount.setRoles(Set.of(customerRole));

            accountRepository.save(newAccount);

            model.addAttribute("registerDto", new RegisterDto());
            model.addAttribute("success",true);
        }catch (Exception e){
            result.addError(
                    new FieldError("registerDto","username",
                            e.getMessage())
            );
        }
        return "register";
    }

    @GetMapping("/register-driver")
    public String registerDriver(Model model){
        RegisterDto registerDto = new RegisterDto();
        model.addAttribute(registerDto);
        model.addAttribute("success",false);
        return "register";
    }

    @PostMapping("/register-driver")
    public String registerDriver(@Valid @ModelAttribute RegisterDto registerDto, BindingResult result, Model model){
        if (!registerDto.getPassword().equals(registerDto.getConfirmPassword())){
            result.addError(
                    new FieldError("registerDto","confirmPassword",
                            "Password and Confirm password do not match")
            );
        }
        Account account = accountRepository.findByEmail(registerDto.getEmail());
        if (account!=null){
            result.addError(
                    new FieldError("registerDto","email",
                            "Email address is already used")
            );
        }
        if (result.hasErrors()){
            return "register";
        }
        try{
            var bCryptEncoder = new BCryptPasswordEncoder();

            Account newAccount = new Account();
            newAccount.setUsername(registerDto.getUsername());
            newAccount.setEmail(registerDto.getEmail());
            newAccount.setPassword(bCryptEncoder.encode(registerDto.getPassword()));
            newAccount.setEnabled(true);

            // Xét role là CarOwner
            Role carOwnerRole = roleRepository.findByName("CarOwner");
            newAccount.setRoles(Set.of(carOwnerRole));

            accountRepository.save(newAccount);

            model.addAttribute("registerDto", new RegisterDto());
            model.addAttribute("success",true);
        }catch (Exception e){
            result.addError(
                    new FieldError("registerDto","username",
                            e.getMessage())
            );
        }
        return "register";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping("/login")
    public String login(String username, String password, Model model) {
        Account account = accountRepository.findByEmail(username);
        if (account == null) {
            account = accountRepository.findByUsername(username);
        }

        if (account != null && passwordEncoder.matches(password, account.getPassword())) {
            if (account.getRoles().stream().anyMatch(role -> role.getName().equals("CarOwner"))) {
                return "redirect:/home-driver";
            }
            return "redirect:/home";
        } else {
            model.addAttribute("error", "Invalid username or password");
            return "login";
        }
    }
}
