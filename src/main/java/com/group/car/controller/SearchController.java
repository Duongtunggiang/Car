package com.group.car.controller;

import com.group.car.models.Car;
import com.group.car.repository.CarRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Controller
@RequestMapping("customer")
public class SearchController {

    @Autowired
    private CarRepository carRepository;

    @GetMapping("/search")
    public String showRentalCarForm(Model model) {
        return "search-result";
    }

    @PostMapping("/search")
    public String searchCars(@RequestParam String pickupLocation,
                             @RequestParam LocalDate pickupDate,
                             @RequestParam LocalTime pickupTime,
                             @RequestParam LocalDate dropoffDate,
                             @RequestParam LocalTime dropoffTime,
                             HttpServletRequest request,
                             Model model) {
        List<Car> searchResults = carRepository.findByAddressContainingIgnoreCase(pickupLocation);
        model.addAttribute("searchResults", searchResults);
        model.addAttribute("pickupLocation", pickupLocation);
        model.addAttribute("pickupDate", pickupDate);
        model.addAttribute("pickupTime", pickupTime);
        model.addAttribute("dropoffDate", dropoffDate);
        model.addAttribute("dropoffTime", dropoffTime);

        // Store the search parameters in the session
        HttpSession session = request.getSession();
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
