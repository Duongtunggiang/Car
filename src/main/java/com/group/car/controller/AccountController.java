package com.group.car.controller;

import com.group.car.models.*;

import com.group.car.models.Dto.RegisterDto;
import com.group.car.repository.AccountRepository;
import com.group.car.repository.CarOwnerRepository;
import com.group.car.repository.CustomerRepository;
import com.group.car.repository.RoleRepository;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.*;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.Date;
import java.util.Set;

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

    //    @GetMapping("/login")
//    public String login() {
//        return "account/login";
//    }
    @GetMapping("/contract")
    public String contract(){
        return "contract";
    }
    @GetMapping("/home")
    public String home() {
        return "home";
    }

    @GetMapping("/home-driver")
    public String homeDriver() {
        return "home-driver";
    }

    @GetMapping("/login")
    public String login(Model model) {
        RegisterDto registerDto = new RegisterDto();
        model.addAttribute("registerDto", registerDto);
        model.addAttribute("error", null);
        return "account/login";
    }

    @PostMapping("/login")
    public String login(@RequestParam("email") String email,
                        @RequestParam("password") String password,
                        Model model) {
        Account account = accountRepository.findByEmail(email);

        if (account != null && passwordEncoder.matches(password, account.getPassword())) {
            if (account.getRoles().stream().anyMatch(role -> role.getName().equals("CarOwner"))) {
                return "redirect:/home-driver";
            } else if (account.getRoles().stream().anyMatch(role -> role.getName().equals("Customer"))) {
                return "redirect:/home";
            }
        }

        model.addAttribute("error", "Invalid email or password");
        model.addAttribute("email", email);
        return "account/login";
    }

    @GetMapping("/login-success")
    public String loginSuccess(Principal principal) {
        Account account = accountRepository.findByEmail(principal.getName());

        if (account != null) {
            if (account.getRoles().stream().anyMatch(role -> role.getName().equals("CarOwner"))) {
                return "redirect:/home-driver";
            } else if (account.getRoles().stream().anyMatch(role -> role.getName().equals("Customer"))) {
                return "redirect:/";
            }
        }

        return "redirect:/login";
    }



    @PostMapping("/register")
    public String registerCustomer(@Valid @ModelAttribute RegisterDto registerDto, BindingResult result, Model model) {
        if (!registerDto.getPassword().equals(registerDto.getConfirmPassword())) {
            result.addError(new FieldError("registerDto", "confirmPassword", "Password and Confirm password do not match"));
        }
        Account account = accountRepository.findByEmail(registerDto.getEmail());
        if (account != null) {
            result.addError(new FieldError("registerDto", "email", "Email address is already used"));
        }
        Account user = accountRepository.findByUsername(registerDto.getUsername());
        if (user != null) {
            result.addError(new FieldError("registerDto", "username", "User name is already used"));
        }
//        if (result.hasErrors()) {
//            return "account/login";
//        }
        if (result.hasErrors()) {
            model.addAttribute("success", false);
            return "account/login";
        }
        try {
            var bCryptEncoder = new BCryptPasswordEncoder();

            Account newAccount = new Account();
            newAccount.setUsername(registerDto.getUsername());
            newAccount.setEmail(registerDto.getEmail());
            newAccount.setPassword(bCryptEncoder.encode(registerDto.getPassword()));
            newAccount.setEnabled(true);

            Role role = roleRepository.findByName("Customer");
            newAccount.setRoles(Set.of(role));

            accountRepository.save(newAccount);

            Customer customer = new Customer();
            customer.setName(registerDto.getUsername());
            customer.setEmail(registerDto.getEmail());
            customer.setAccount(newAccount);

            customerRepository.save(customer);

            model.addAttribute("registerDto", new RegisterDto());
            model.addAttribute("success", true);
        } catch (Exception e) {
            result.addError(new FieldError("registerDto", "username", e.getMessage()));
        }
        return "account/login";
    }

    @PostMapping("/register-driver")
    public String registerCarOwner(@Valid @ModelAttribute RegisterDto registerDto, BindingResult result, Model model) {
        if (!registerDto.getPassword().equals(registerDto.getConfirmPassword())) {
            result.addError(new FieldError("registerDto", "confirmPassword", "Password and Confirm password do not match"));
        }
        Account account = accountRepository.findByEmail(registerDto.getEmail());
        if (account != null) {
            result.addError(new FieldError("registerDto", "email", "Email address is already used"));
        }
        Account user = accountRepository.findByUsername(registerDto.getUsername());
        if (user != null) {
            result.addError(new FieldError("registerDto", "username", "User name is already used"));
        }
        if (result.hasErrors()) {
            model.addAttribute("success", false);
            return "account/login";
        }
        try {
            var bCryptEncoder = new BCryptPasswordEncoder();

            Account newAccount = new Account();
            newAccount.setUsername(registerDto.getUsername());
            newAccount.setEmail(registerDto.getEmail());
            newAccount.setPassword(bCryptEncoder.encode(registerDto.getPassword()));
            newAccount.setEnabled(true);

            Role role = roleRepository.findByName("CarOwner");
            newAccount.setRoles(Set.of(role));

            accountRepository.save(newAccount);

            CarOwner carOwner = new CarOwner();
            carOwner.setName(registerDto.getUsername());
            carOwner.setEmail(registerDto.getEmail());
            carOwner.setAccount(newAccount);

            carOwnerRepository.save(carOwner);

            model.addAttribute("registerDto", new RegisterDto());
            model.addAttribute("success", true);
        } catch (Exception e) {
            result.addError(new FieldError("registerDto", "username", e.getMessage()));
        }
        return "account/login";
    }

//    @GetMapping("/register")
//    public String register(Model model){
//        RegisterDto registerDto = new RegisterDto();
//        model.addAttribute(registerDto);
//        model.addAttribute("success", false);
//        return "account/register";
//    }

//    @GetMapping("/register-driver")
//    public String registerDriver(Model model){
//        RegisterDto registerDto = new RegisterDto();
//        model.addAttribute(registerDto);
//        model.addAttribute("success", false);
//        return "account/register-driver";
//    }


    // Forgot password
    @GetMapping("/forgot-password")
    public String forgot() {
        return "account/forgot-password";
    }

    @PostMapping("/forgot-password")
    public String forgotPassword(@RequestParam String email, Model model) {
        Account account = accountRepository.findByEmail(email);
        if (account != null) {
            RegisterDto registerDto = new RegisterDto();
            model.addAttribute("registerDto", registerDto);
            model.addAttribute("email", email);
            return "account/reset-password";
        }
        model.addAttribute("errorMessage", "Email cannot find.");
        return "account/forgot-password";
    }

    @GetMapping("/reset-password")
    public String showResetPasswordForm(@RequestParam("email") String email, Model model) {
        RegisterDto registerDto = new RegisterDto();
        model.addAttribute("registerDto", registerDto);
        model.addAttribute("email", email);
        return "account/reset-password";
    }

    @PostMapping("/reset-password")
    public String resetPassword(@ModelAttribute("registerDto") RegisterDto registerDto,
                                @RequestParam String email,
                                BindingResult result,
                                Model model) {
        try {
            if (!registerDto.getPassword().equals(registerDto.getConfirmPassword())) {
                result.rejectValue("confirmPassword", "registerDto", "Password and Confirm password do not match!!");
            }

            if (result.hasErrors()) {
                model.addAttribute("email", email);
                return "account/reset-password";
            }
            var bCryptEncoder = new BCryptPasswordEncoder();
            Account account = accountRepository.findByEmail(email);
            if (account != null) {
                account.setPassword(bCryptEncoder.encode(registerDto.getPassword()));
//                account.setPassword(passwordEncoder.encode(registerDto.getPassword()));
                accountRepository.save(account);
                model.addAttribute("successMessage", "The password has been successfully reseted.");
                return "account/reset-password";
            }
            model.addAttribute("errorMessage", "Email cannot find.");
            model.addAttribute("email", email);
        }catch (Exception e){
            result.addError(new FieldError("registerDto", "password", e.getMessage()));
        }
        return "account/reset-password";
    }

    @GetMapping("/change")
    public String change(Model model) {
        model.addAttribute("registerDto", new RegisterDto());
        setUpUserRole(model);
        model.addAttribute("currentPage", "change-password");
        return "account/change-password";
    }

    @PostMapping("/change-password")
    public String changePassword(
            @ModelAttribute("registerDto") RegisterDto registerDto,
            BindingResult result,
            Model model) {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();

            if (!registerDto.getPassword().equals(registerDto.getConfirmPassword())) {
                result.rejectValue("confirmPassword", "error.registerDto", "Password and Confirm password do not match");
                model.addAttribute("registerDto", registerDto);
                return "account/change-password";
            }
            if (result.hasErrors()) {
                model.addAttribute("registerDto", registerDto);
                return "account/change-password";
            }
            Account account = accountRepository.findByEmail(email);
            var bCryptEncoder = new BCryptPasswordEncoder();
            if (account != null) {
                account.setPassword(bCryptEncoder.encode(registerDto.getPassword()));
//            account.setPassword(passwordEncoder.encode(registerDto.getPassword()));
                accountRepository.save(account);
                model.addAttribute("successMessage", "Mật khẩu đã được đặt lại thành công.");
                model.addAttribute("registerDto", new RegisterDto());
                return "account/change-password";
            }

            model.addAttribute("registerDto", registerDto);
        }catch (Exception e){
            result.addError(new FieldError("registerDto", "password", e.getMessage()));
        }
        return "account/change-password";
    }

    @GetMapping("/profile")
    public String getProfile(Model model, Principal principal) {
        String email = principal.getName();
        Account emailAccount = accountRepository.findByEmail(email);

        if (emailAccount == null) {
            return "redirect:/login";
        }

        String homeUrl = "/";

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
            homeUrl = "/home-driver";
        }

        model.addAttribute("homeUrl", homeUrl);
        setUpUserRole(model);
        model.addAttribute("currentPage", "profile");
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
        setUpUserRole(model);
        model.addAttribute("currentPage", "edit-profile");
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
//                                @RequestParam int wallet,
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
//                existingCustomer.setWallet(wallet);
                customerRepository.save(existingCustomer);
            } else if (existingAccount.getRoles().stream().anyMatch(role -> role.getName().equals("CarOwner"))) {
                CarOwner existingCarOwner = carOwnerRepository.findByAccountId(existingAccount.getId());
//                existingCarOwner.setName(name);
                existingCarOwner.setDateOfBirth(dateOfBirth);
                existingCarOwner.setNationalIdNo(nationalIdNo);
                existingCarOwner.setPhoneNo(phoneNo);
                existingCarOwner.setAddress(address);
                existingCarOwner.setDrivingLicense(drivingLicense);
//                existingCarOwner.setWallet(wallet);
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


}
