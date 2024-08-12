package com.group.car.controller;

import com.group.car.models.Car;
import com.group.car.repository.CarRepository;
import com.group.car.service.CarService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("customer")
public class SearchController {
    @Autowired
    private CarService carService;


    @Autowired
    private CarRepository carRepository;

    @GetMapping("/search")
    public String showRentalCarForm(Model model) {
        return "search-result";
    }

    @PostMapping("/search")
    public String searchCars(@RequestParam String pickupLocation,
                             @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate pickupDate,
                             @RequestParam @DateTimeFormat(pattern = "HH:mm") LocalTime pickupTime,
                             @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dropoffDate,
                             @RequestParam @DateTimeFormat(pattern = "HH:mm") LocalTime dropoffTime,
                             Model model, HttpSession session) {
        LocalDateTime startDateTime = LocalDateTime.of(pickupDate, pickupTime);
        LocalDateTime endDateTime = LocalDateTime.of(dropoffDate, dropoffTime);

        Date startDate = Date.from(startDateTime.atZone(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(endDateTime.atZone(ZoneId.systemDefault()).toInstant());

        List<Car> searchResults = carService.searchAvailableCars(pickupLocation, startDate, endDate);

        model.addAttribute("searchResults", searchResults);
        model.addAttribute("pickupLocation", pickupLocation);
        model.addAttribute("pickupDate", pickupDate);
        model.addAttribute("pickupTime", pickupTime);
        model.addAttribute("dropoffDate", dropoffDate);
        model.addAttribute("dropoffTime", dropoffTime);

        // Store search criteria in session for later use
        session.setAttribute("pickupLocation", pickupLocation);
        session.setAttribute("pickupDate", pickupDate);
        session.setAttribute("pickupTime", pickupTime);
        session.setAttribute("dropoffDate", dropoffDate);
        session.setAttribute("dropoffTime", dropoffTime);

        return "search-result";
    }

    @GetMapping("/car-details")
    public String showCarDetails(@RequestParam Long id, Model model) {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Car not found"));
        model.addAttribute("car", car);
        return "customer/car-details";
    }
}
