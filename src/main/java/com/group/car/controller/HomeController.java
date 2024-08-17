package com.group.car.controller;
import com.group.car.models.CarBooking;


import com.group.car.models.Account;
import com.group.car.models.Booking;
import com.group.car.models.Car;
import com.group.car.models.Role;
import com.group.car.repository.AccountRepository;
import com.group.car.repository.BookingRepository;
import com.group.car.repository.CarBookingRepository;
import com.group.car.repository.CarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
public class HomeController {

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private CarBookingRepository carBookingRepository;

    @GetMapping({"", "/"})
    public String home(Model model) {
        List<Booking> availableBookings = bookingRepository.findByStatus("Available");
        List<Car> availableCars = availableBookings.stream()
                .flatMap(booking -> booking.getCarBookings().stream())
                .map(CarBooking::getCar)
                .distinct()
                .collect(Collectors.toList());

        Map<Long, String> carStatusMap = new HashMap<>();

        for (Car car : availableCars) {
            List<CarBooking> carBookings = carBookingRepository.findByCarId(car.getId());
            if (!carBookings.isEmpty()) {
                String status = carBookings.get(0).getBooking().getStatus();
                carStatusMap.put(car.getId(), status);
            } else {
                carStatusMap.put(car.getId(), "No Booking");
            }
        }

        model.addAttribute("cars", availableCars);
        model.addAttribute("carStatusMap", carStatusMap);
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
