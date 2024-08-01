package com.group.car.controller;

import com.group.car.models.Account;
import com.group.car.models.Car;
import com.group.car.models.Role;
import com.group.car.repository.AccountRepository;
import com.group.car.repository.CarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Set;

@Controller
public class HomeController {

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private AccountRepository accountRepository;

    @GetMapping({"","/"})
    public String home(
    Model model){
        List<Car> cars = carRepository.findAll();
        model.addAttribute("cars", cars);
        setUpUserRole(model);
        model.addAttribute("currentPage", "home");
        return "home";
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
