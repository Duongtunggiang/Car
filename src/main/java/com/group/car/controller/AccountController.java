package com.group.car.controller;

import com.group.car.models.*;

import com.group.car.repository.AccountRepository;
import com.group.car.repository.CarOwnerRepository;
import com.group.car.repository.CustomerRepository;
import com.group.car.repository.RoleRepository;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.*;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Date;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Controller
public class AccountController {
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CarOwnerRepository carOwnerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/register")
    public String register(Model model){
        RegisterDto registerDto = new RegisterDto();
        model.addAttribute(registerDto);
        model.addAttribute("success", false);
        return "account/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute RegisterDto registerDto, BindingResult result, Model model){
        if (!registerDto.getPassword().equals(registerDto.getConfirmPassword())){
            result.addError(new FieldError("registerDto", "confirmPassword", "Password and Confirm password do not match"));
        }
        Account account = accountRepository.findByEmail(registerDto.getEmail());
        if (account != null){
            result.addError(new FieldError("registerDto", "email", "Email address is already used"));
        }
        Account user = accountRepository.findByUsername(registerDto.getUsername());
        if (user != null){
            result.addError(new FieldError("registerDto", "username", "User name is already used"));
        }
        if (result.hasErrors()){
            return "account/register";
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

            // Tạo Customer và gán Account
            Customer customer = new Customer();
            customer.setName(registerDto.getUsername());
            customer.setEmail(registerDto.getEmail());
            customer.setAccount(newAccount);

            customerRepository.save(customer);

            model.addAttribute("registerDto", new RegisterDto());
            model.addAttribute("success", true);
        }catch (Exception e){
            result.addError(new FieldError("registerDto", "username", e.getMessage()));
        }
        return "account/register";
    }

    @GetMapping("/register-driver")
    public String registerDriver(Model model){
        RegisterDto registerDto = new RegisterDto();
        model.addAttribute(registerDto);
        model.addAttribute("success", false);
        return "account/register-driver";
    }

    @PostMapping("/register-driver")
    public String registerDriver(@Valid @ModelAttribute RegisterDto registerDto, BindingResult result, Model model){
        if (!registerDto.getPassword().equals(registerDto.getConfirmPassword())){
            result.addError(new FieldError("registerDto", "confirmPassword", "Password and Confirm password do not match"));
        }
        Account account = accountRepository.findByEmail(registerDto.getEmail());
        if (account != null){
            result.addError(new FieldError("registerDto", "email", "Email address is already used"));
        }
        Account user = accountRepository.findByUsername(registerDto.getUsername());
        if (user != null){
            result.addError(new FieldError("registerDto", "username", "User name is already used"));
        }
        if (result.hasErrors()){
            return "account/register-driver";
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

            // Tạo CarOwner và gán Account
            CarOwner carOwner = new CarOwner();
            carOwner.setName(registerDto.getUsername());
            carOwner.setEmail(registerDto.getEmail());
            carOwner.setAccount(newAccount);

            carOwnerRepository.save(carOwner);

            model.addAttribute("registerDto", new RegisterDto());
            model.addAttribute("success", true);
        }catch (Exception e){
            result.addError(new FieldError("registerDto", "username", e.getMessage()));
        }
        return "account/register-driver";
    }
    @GetMapping("/login")
    public String login() {
        return "account/login";
    }
    @GetMapping("/contract")
    public String contract(){
        return "contract";
    }
    @GetMapping("/home")
    public String homes(){
        return "home";
    }
    @GetMapping("/home-driver")
    public String homeDriver(){
        return "home-driver";
    }

    @PostMapping("/login")
    public String login(@RequestParam("username") String username,
                        @RequestParam("password") String password,
                        Model model) {
        Account account = accountRepository.findByEmail(username);
        if (account == null) {
            account = accountRepository.findByUsername(username);
        }

        if (account != null && passwordEncoder.matches(password, account.getPassword())) {
            if (account.getRoles().stream().anyMatch(role -> role.getName().equals("CarOwner"))) {
                return "redirect:/home-driver";
            } else if (account.getRoles().stream().anyMatch(role -> role.getName().equals("Customer"))) {
                return "redirect:/home";
            }
        }

        model.addAttribute("error", "Invalid username or password");
        return "account/login";
    }

    // Forgot password
    @GetMapping("/forgot-password")
    public String forgot() {
        return "account/forgot-password";
    }

    @PostMapping("/forgot-password")
    public String forgotPassword(@RequestParam String email, Model model) {
        Account account = accountRepository.findByEmail(email);
        if (account != null) {
            model.addAttribute("email", email);
            return "account/verify-password";
        }
        model.addAttribute("errorMessage", "Email không tìm thấy.");
        return "account/forgot-password";
    }

    // Verify password
    @PostMapping("/verify-password")
    public String verifyPassword(@RequestParam String email, @RequestParam String oldPassword, Model model) {
        Account account = accountRepository.findByEmail(email);
        if (account != null && comparePasswords(oldPassword, account.getPassword())) {
            model.addAttribute("email", email);
            return "account/reset-password";
        }
        model.addAttribute("errorMessage", "Xác minh mật khẩu thất bại.");
        model.addAttribute("email", email);
        return "account/verify-password";
    }

    private boolean comparePasswords(String inputPassword, String storedPassword) {
        return passwordEncoder.matches(inputPassword, storedPassword);
    }

    // Reset password
    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam String email, @RequestParam String newPassword, Model model) {
        Account account = accountRepository.findByEmail(email);
        if (account != null) {
            account.setPassword(passwordEncoder.encode(newPassword));
            accountRepository.save(account);
            model.addAttribute("successMessage", "Mật khẩu đã được đặt lại thành công.");
            return "login";
        }
        model.addAttribute("errorMessage", "Email không tìm thấy.");
        return "account/reset-password";
    }

    @GetMapping("/profile")
    public String getProfile(Model model, Principal principal) {
        String email = principal.getName();
        Account emailAccount = accountRepository.findByEmail(email);

        if (emailAccount == null) {
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

//        // Logging for debugging
//        System.out.println("Email Account: " + emailAccount);
//        System.out.println("Customer: " + model.getAttribute("customer"));
//        System.out.println("CarOwner: " + model.getAttribute("carOwner"));

        return "account/profile";
    }

    @GetMapping("/edit-profile")
    public String showEditProfile(Model model, Principal principal) {
        Account account = accountRepository.findByEmail(principal.getName());
        model.addAttribute("account", account);

        if (account.getRoles().stream().anyMatch(role -> role.getName().equals("Customer"))) {
            model.addAttribute("customer", account.getCustomer());
            model.addAttribute("carOwner", null);
        } else if (account.getRoles().stream().anyMatch(role -> role.getName().equals("CarOwner"))) {
            model.addAttribute("customer", null);
            model.addAttribute("carOwner", account.getCarOwner());
        }
        return "account/edit-profile";
    }

    @PostMapping("/edit-profile")
    public String updateProfile(@RequestParam String username,
                                @RequestParam String email,
                                @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date dateOfBirth,
                                @RequestParam String nationalIdNo,
                                @RequestParam String phoneNo,
                                @RequestParam String address,
                                @RequestParam String drivingLicense,
                                @RequestParam String wallet,
                                Principal principal) {
        try {
            Account existingAccount = accountRepository.findByEmail(principal.getName());
            existingAccount.setUsername(username);
            existingAccount.setEmail(email);

            if (existingAccount.getRoles().stream().anyMatch(role -> role.getName().equals("Customer"))) {
                Customer existingCustomer = customerRepository.findByAccountId(existingAccount.getId());
//                existingCustomer.setName(name);
                existingCustomer.setDateOfBirth(dateOfBirth);
                existingCustomer.setNationalIdNo(nationalIdNo);
                existingCustomer.setPhoneNo(phoneNo);
                existingCustomer.setAddress(address);
                existingCustomer.setDrivingLicense(drivingLicense);
                existingCustomer.setWallet(wallet);
                customerRepository.save(existingCustomer);
            } else if (existingAccount.getRoles().stream().anyMatch(role -> role.getName().equals("CarOwner"))) {
                CarOwner existingCarOwner = carOwnerRepository.findByAccountId(existingAccount.getId());
//                existingCarOwner.setName(name);
                existingCarOwner.setDateOfBirth(dateOfBirth);
                existingCarOwner.setNationalIdNo(nationalIdNo);
                existingCarOwner.setPhoneNo(phoneNo);
                existingCarOwner.setAddress(address);
                existingCarOwner.setDrivingLicense(drivingLicense);
                existingCarOwner.setWallet(wallet);
                carOwnerRepository.save(existingCarOwner);
            }

            accountRepository.save(existingAccount);

        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
        return "redirect:/profile";
    }


    @ExceptionHandler(Exception.class)
    public String handleException(Exception ex, Model model) {
        model.addAttribute("errorMessage", ex.getMessage());
        return "error";
    }


    @GetMapping("/change")
    public String change(){
        return "account/change-password";
    }
    @PostMapping("/change")
    public String changePassword(@RequestParam String currentPassword,
                                 @RequestParam String newPassword,
                                 @RequestParam String confirmPassword,
                                 Authentication authentication,
                                 Model model) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Account account = accountRepository.findByEmail(userDetails.getUsername());

        // Check current password
        if (!passwordEncoder.matches(currentPassword, account.getPassword())) {
            model.addAttribute("currentPasswordError", "Current password is incorrect.");
            return "account/change-password";
        }

        // Check if new password matches confirm password
        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("confirmPasswordError", "New passwords do not match.");
            return "account/change-password";
        }

        // Update password
        account.setPassword(passwordEncoder.encode(newPassword));
        accountRepository.save(account);

        return "redirect:/profile";
    }
}
