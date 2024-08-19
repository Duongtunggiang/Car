package com.group.car.controller;

import com.group.car.models.Car;
import com.group.car.models.CarBooking;
import com.group.car.repository.CarBookingRepository;
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
import java.util.*;

@Controller
@RequestMapping("customer")
public class SearchController {
    @Autowired
    private CarService carService;


    @Autowired
    private CarRepository carRepository;
    @Autowired
    private CarBookingRepository carBookingRepository;

//    @GetMapping("/search")
//    public String showRentalCarForm(Model model) {
//        return "search-result";
//    }

//    @GetMapping("/search")
//    public String showRentalCarForm(@RequestParam(required = false) String pickupLocation, Model model) {
//        if (pickupLocation != null && !pickupLocation.isEmpty()) {
//            List<Car> searchResults = carRepository.findByAddressContainingIgnoreCase(pickupLocation);
//            model.addAttribute("searchResults", searchResults);
//        }
//        model.addAttribute("pickupLocation", pickupLocation);
//        return "search-result";
//    }
//
//    @PostMapping("/search")
//    public String searchCars(@RequestParam String pickupLocation,
//                             @RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate pickupDate,
//                             @RequestParam @DateTimeFormat(pattern = "HH:mm") LocalTime pickupTime,
//                             @RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate dropoffDate,
//                             @RequestParam @DateTimeFormat(pattern = "HH:mm") LocalTime dropoffTime,
//                             Model model, HttpSession session) {
//        LocalDateTime startDateTime = LocalDateTime.of(pickupDate, pickupTime);
//        LocalDateTime endDateTime = LocalDateTime.of(dropoffDate, dropoffTime);
//
//        Date startDate = Date.from(startDateTime.atZone(ZoneId.systemDefault()).toInstant());
//        Date endDate = Date.from(endDateTime.atZone(ZoneId.systemDefault()).toInstant());
//
//        List<Car> searchResults = carService.searchAvailableCars(pickupLocation, startDate, endDate);
//
//        // Get booking dates for each car
//        Map<Long, List<Map<String, Date>>> carBookingDates = new HashMap<>();
//        for (Car car : searchResults) {
//            List<Map<String, Date>> bookingDates = carBookingRepository.findBookingDatesByCar(car.getId());
//            carBookingDates.put(car.getId(), bookingDates);
//        }
//
//        model.addAttribute("searchResults", searchResults);
//        model.addAttribute("carBookingDates", carBookingDates);
//        model.addAttribute("pickupLocation", pickupLocation);
//        model.addAttribute("pickupDate", pickupDate);
//        model.addAttribute("pickupTime", pickupTime);
//        model.addAttribute("dropoffDate", dropoffDate);
//        model.addAttribute("dropoffTime", dropoffTime);
//
//        // Store search criteria in session for later use
//        session.setAttribute("pickupLocation", pickupLocation);
//        session.setAttribute("pickupDate", pickupDate);
//        session.setAttribute("pickupTime", pickupTime);
//        session.setAttribute("dropoffDate", dropoffDate);
//        session.setAttribute("dropoffTime", dropoffTime);
//
//        return "search-result";
//    }

//    @PostMapping("/search")
//    public String searchCars(@RequestParam String pickupLocation,
//                             @RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate pickupDate,
//                             @RequestParam @DateTimeFormat(pattern = "HH:mm") LocalTime pickupTime,
//
//                             @RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate dropoffDate,
//                             @RequestParam @DateTimeFormat(pattern = "HH:mm") LocalTime dropoffTime,
//                             Model model, HttpSession session) {
//        LocalDateTime startDateTime = LocalDateTime.of(pickupDate, pickupTime);
//        LocalDateTime endDateTime = LocalDateTime.of(dropoffDate, dropoffTime);
//
//        Date startDate = Date.from(startDateTime.atZone(ZoneId.systemDefault()).toInstant());
//        Date endDate = Date.from(endDateTime.atZone(ZoneId.systemDefault()).toInstant());
//
//        List<Car> searchResults = carService.searchAvailableCars(pickupLocation, startDate, endDate);
//
//        model.addAttribute("searchResults", searchResults);
//        model.addAttribute("pickupLocation", pickupLocation);
//        model.addAttribute("pickupDate", pickupDate);
//        model.addAttribute("pickupTime", pickupTime);
//        model.addAttribute("dropoffDate", dropoffDate);
//        model.addAttribute("dropoffTime", dropoffTime);
//
//        // Store search criteria in session for later use
//        session.setAttribute("pickupLocation", pickupLocation);
//        session.setAttribute("pickupDate", pickupDate);
//        session.setAttribute("pickupTime", pickupTime);
//        session.setAttribute("dropoffDate", dropoffDate);
//        session.setAttribute("dropoffTime", dropoffTime);
//
//        return "search-result";
//    }


    @GetMapping("/search")
    public String showRentalCarForm(@RequestParam(required = false) String pickupLocation, Model model) {
        if (pickupLocation == null || pickupLocation.isEmpty()) {
            pickupLocation = "Ha Noi";  // Default to Ha Noi if no location is specified
        }
        List<Car> searchResults = carRepository.findByAddressContainingIgnoreCase(pickupLocation);

        Map<Long, List<Map<String, Date>>> carBookingDates = getCarBookingDates(searchResults);
        Map<Long, Long> carBookingCountMap = getCarBookingCountMap(searchResults);

        model.addAttribute("searchResults", searchResults);
        model.addAttribute("carBookingDates", carBookingDates);
        model.addAttribute("carBookingCountMap", carBookingCountMap);
        model.addAttribute("pickupLocation", pickupLocation);
        model.addAttribute("isDateSearch", false);

        return "search-result";
    }

    private Map<Long, Long> getCarBookingCountMap(List<Car> cars) {
        Map<Long, Long> carBookingCountMap = new HashMap<>();
        for (Car car : cars) {
            List<CarBooking> carBookings = carBookingRepository.findByCarId(car.getId());
            long confirmedBookingCount = carBookings.stream()
                    .filter(cb -> "Completed".equalsIgnoreCase(cb.getBooking().getStatus()))
                    .count();
            carBookingCountMap.put(car.getId(), confirmedBookingCount);
        }
        return carBookingCountMap;
    }

//    @PostMapping("/search")
//    public String searchCars(@RequestParam String pickupLocation,
//                             @RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate pickupDate,
//                             @RequestParam @DateTimeFormat(pattern = "HH:mm") LocalTime pickupTime,
//                             @RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate dropoffDate,
//                             @RequestParam @DateTimeFormat(pattern = "HH:mm") LocalTime dropoffTime,
//                             Model model, HttpSession session) {
//        LocalDateTime startDateTime = LocalDateTime.of(pickupDate, pickupTime);
//        LocalDateTime endDateTime = LocalDateTime.of(dropoffDate, dropoffTime);
//
//        Date startDate = Date.from(startDateTime.atZone(ZoneId.systemDefault()).toInstant());
//        Date endDate = Date.from(endDateTime.atZone(ZoneId.systemDefault()).toInstant());
//
//        List<Car> searchResults = carService.searchAvailableCars(pickupLocation, startDate, endDate);
//
//        Map<Long, List<Map<String, Date>>> carBookingDates = getCarBookingDates(searchResults);
//
//        model.addAttribute("searchResults", searchResults);
//        model.addAttribute("carBookingDates", carBookingDates);
//        model.addAttribute("pickupLocation", pickupLocation);
//        model.addAttribute("pickupDate", pickupDate);
//        model.addAttribute("pickupTime", pickupTime);
//        model.addAttribute("dropoffDate", dropoffDate);
//        model.addAttribute("dropoffTime", dropoffTime);
//        model.addAttribute("isDateSearch", true);
//
//        // Store search criteria in session for later use
//        session.setAttribute("pickupLocation", pickupLocation);
//        session.setAttribute("pickupDate", pickupDate);
//        session.setAttribute("pickupTime", pickupTime);
//        session.setAttribute("dropoffDate", dropoffDate);
//        session.setAttribute("dropoffTime", dropoffTime);
//
//        return "search-result";
//    }

    @PostMapping("/search")
    public String searchCars(@RequestParam String pickupLocation,
                             @RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate pickupDate,
                             @RequestParam @DateTimeFormat(pattern = "HH:mm") LocalTime pickupTime,
                             @RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate dropoffDate,
                             @RequestParam @DateTimeFormat(pattern = "HH:mm") LocalTime dropoffTime,
                             @RequestParam(required = false) Long selectedCarId,
                             Model model, HttpSession session) {
        LocalDateTime startDateTime = LocalDateTime.of(pickupDate, pickupTime);
        LocalDateTime endDateTime = LocalDateTime.of(dropoffDate, dropoffTime);

        Date startDate = Date.from(startDateTime.atZone(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(endDateTime.atZone(ZoneId.systemDefault()).toInstant());

        List<Car> searchResults;
        if (selectedCarId != null) {
            Car selectedCar = carRepository.findById(selectedCarId).orElse(null);
            searchResults = selectedCar != null ? Collections.singletonList(selectedCar) : new ArrayList<>();
        } else {
            searchResults = carService.searchAvailableCars(pickupLocation, startDate, endDate);
        }

        Map<Long, List<Map<String, Date>>> carBookingDates = getCarBookingDates(searchResults);

        model.addAttribute("searchResults", searchResults);
        model.addAttribute("carBookingDates", carBookingDates);
        model.addAttribute("pickupLocation", pickupLocation);
        model.addAttribute("pickupDate", pickupDate);
        model.addAttribute("pickupTime", pickupTime);
        model.addAttribute("dropoffDate", dropoffDate);
        model.addAttribute("dropoffTime", dropoffTime);
        model.addAttribute("isDateSearch", true);
        model.addAttribute("selectedCarId", selectedCarId);

        // Store search criteria in session for later use
        session.setAttribute("pickupLocation", pickupLocation);
        session.setAttribute("pickupDate", pickupDate);
        session.setAttribute("pickupTime", pickupTime);
        session.setAttribute("dropoffDate", dropoffDate);
        session.setAttribute("dropoffTime", dropoffTime);
        session.setAttribute("selectedCarId", selectedCarId);

        return "search-result";
    }

    private Map<Long, List<Map<String, Date>>> getCarBookingDates(List<Car> cars) {
        Map<Long, List<Map<String, Date>>> carBookingDates = new HashMap<>();
        for (Car car : cars) {
            List<Map<String, Date>> bookingDates = carBookingRepository.findBookingDatesByCar(car.getId());
            carBookingDates.put(car.getId(), bookingDates);
        }
        return carBookingDates;
    }

    @GetMapping("/car-details")
    public String showCarDetails(@RequestParam Long id, Model model) {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Car not found"));

        List<CarBooking> carBookings = carBookingRepository.findByCarId(car.getId());
        String status = "Available";
        if (!carBookings.isEmpty()) {
            status = carBookings.get(0).getBooking().getStatus();
        }

        model.addAttribute("car", car);
        model.addAttribute("status", status);

        return "customer/car-details";
    }




}