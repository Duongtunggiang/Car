package com.group.car.controller;

import com.group.car.models.*;
import com.group.car.models.Dto.CarDto;
import com.group.car.repository.*;
import com.group.car.service.BookingService;
import com.group.car.service.CarService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.*;


@Controller
@RequestMapping("/car-owner")
public class CarOwnerController {
    @Autowired
    private CarRepository iCarService;

    @Autowired
    private CarOwnerRepository carOwnerRepository;

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private CarService carService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private BookingService bookingService;
    @Autowired
    private CarBookingRepository carBookingRepository;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private CustomerRepository customerRepository;
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

    @GetMapping("/my-reports")
    public String viewFeedbackReport(Model model, Principal principal) {
        String email = principal.getName();
        Optional<CarOwner> carOwnerOptional = carOwnerRepository.findByEmail(email);

        if (carOwnerOptional.isPresent()) {
            CarOwner carOwner = carOwnerOptional.get();
            List<Car> cars = carService.findAllByCarOwner(carOwner.getId());

            List<Feedback> allFeedbacks = new ArrayList<>();
            Map<Integer, Long> feedbackCounts = new HashMap<>();
            double totalRating = 0;
            long totalFeedbacks = 0;

            for (Car car : cars) {
                for (CarBooking carBooking : car.getCarBookings()) {
                    Feedback feedback = carBooking.getBooking().getFeedback();
                    if (feedback != null) {
                        allFeedbacks.add(feedback);
                        int rating = feedback.getRatings();
                        totalRating += rating;
                        totalFeedbacks++;
                        feedbackCounts.put(rating, feedbackCounts.getOrDefault(rating, 0L) + 1);
                    }
                }
            }

            // Calculate average rating
            double averageRating = totalFeedbacks > 0 ? totalRating / totalFeedbacks : 0;

            // Round the average rating to 1 decimal place
            averageRating = Math.round(averageRating * 10.0) / 10.0;

            model.addAttribute("averageRating", averageRating);
            model.addAttribute("feedbacks", allFeedbacks);
            model.addAttribute("feedbackCounts", feedbackCounts);
            setUpUserRole(model);
            model.addAttribute("currentPage", "my-reports");
            return "car-owner/my-reports";
        } else {
            return "redirect:/login";
        }
    }


    @GetMapping({"/my-cars"})
    public String showAllCar(Model model, Principal principal) {
        String email = principal.getName();

        Optional<CarOwner> carOwnerOptional = carOwnerRepository.findByEmail(email);

        if (carOwnerOptional.isPresent()) {
            CarOwner carOwner = carOwnerOptional.get();
            List<Car> cars = carService.findAllByCarOwner(carOwner.getId());

            Map<Long, String> carStatusMap = new HashMap<>();
            Map<Long, Long> carBookingCountMap = new HashMap<>();
            Map<Long, Integer> carRatingMap = new HashMap<>();

            for (Car car : cars) {
                List<CarBooking> carBookings = carBookingRepository.findByCarId(car.getId());
                if (!carBookings.isEmpty()) {
                    String status = carBookings.get(0).getBooking().getStatus();
                    carStatusMap.put(car.getId(), status);
                } else {
                    carStatusMap.put(car.getId(), "Available");
                }

                // Count only confirmed bookings
                long confirmedBookingCount = carBookings.stream()
                        .filter(cb -> "Completed".equalsIgnoreCase(cb.getBooking().getStatus()))
                        .count();
                carBookingCountMap.put(car.getId(), confirmedBookingCount);

                Double averageRating = feedbackRepository.getAverageRatingForCar(car.getId());
                carRatingMap.put(car.getId(), averageRating != null ? (int) Math.round(averageRating) : 0);
            }

            model.addAttribute("cars", cars);
            model.addAttribute("carStatusMap", carStatusMap);
            model.addAttribute("carBookingCountMap", carBookingCountMap);
            model.addAttribute("carRatingMap", carRatingMap);

        } else {
            model.addAttribute("error", "No cars found for this user.");
        }

        setUpUserRole(model);
        model.addAttribute("currentPage", "car");
        return "car-owner/my-cars";
    }


    @GetMapping("/add")
    public String showCreateCar(Model model) {
        CarDto carDto = new CarDto();
        model.addAttribute("carDto", carDto);
        setUpUserRole(model);
        model.addAttribute("currentPage", "addCar");

        return "car-owner/add-car2";
    }

    @PostMapping("/add")
    public String addCar(@Valid @ModelAttribute CarDto carDto, BindingResult result, Principal principal) {
        if (carDto.getImages().isEmpty()) {

            result.addError(new FieldError("carDto", "images", "The file is required"));
        }
        if (result.hasErrors()) {
            return "car-owner/add-car2";
        }

        // Save image file
        MultipartFile image = carDto.getImages();
        String storageFile = image.getOriginalFilename();

        try {
            String uploadDir = "public/img/";
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            try (InputStream inputStream = image.getInputStream()) {
                Files.copy(inputStream, Paths.get(uploadDir + storageFile),
                        StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }



        Car car = new Car();
        car.setName(carDto.getName());
        car.setLicensePlate(carDto.getLicensePlate());
        car.setBrand(carDto.getBrand());
        car.setModel(carDto.getModel());
        car.setColor(carDto.getColor());
        car.setNumbersOfSeats(carDto.getNumbersOfSeats());
        car.setProductionYears(carDto.getProductionYears());
        car.setTransmissionType(carDto.getTransmissionType());
        car.setFuelType(carDto.getFuelType());
        car.setMileage(carDto.getMileage());
        car.setFuelConsumption(carDto.getFuelConsumption());
        car.setBasicPrice(carDto.getBasicPrice());
        car.setDeposit(carDto.getDeposit());
        car.setAddress(carDto.getAddress());
        car.setDescription(carDto.getDescription());
        car.setAdditionalFunctions(carDto.getAdditionalFunction());
        car.setTermsOfUse(carDto.getTermsOfUse());
        car.setImages(storageFile);


        CarOwner carOwner = carOwnerRepository.findByAccountEmail(principal.getName());

        if (carOwner == null) {
            result.rejectValue("carOwner", "error.carOwner", "Car owner not found");
            return "car-owner/add-car2";
        }
        car.setCarOwner(carOwner);

        iCarService.save(car);
//
        Booking booking = new Booking();
        booking.setStatus("Available");
        booking.setPickUpLocation(car.getAddress());
        booking.setBookingNo(UUID.randomUUID().toString());
        bookingRepository.save(booking);

        CarBooking carBooking = new CarBooking();
        carBooking.setCar(car);
        carBooking.setBooking(booking);
        carBookingRepository.save(carBooking);

        return "redirect:/car-owner/my-cars";
    }

    @PostMapping("/updateStatus")
    public String updateStatus(@RequestParam Long carId, @RequestParam String status) {
        Car car = carRepository.findById(carId).orElseThrow(() -> new RuntimeException("Car not found"));
        List<CarBooking> carBookings = carBookingRepository.findByCarId(carId);

        for (CarBooking carBooking : carBookings) {
            Booking booking = carBooking.getBooking();
            booking.setStatus(status);
            bookingRepository.save(booking);
        }

        return "redirect:/car-owner/my-cars";
    }



    @GetMapping("/delete")
    public String deleteCar(@RequestParam long id) {
        try {
            Car car = iCarService.findById(id).get();
            // Delete image
            Path imagePath = Paths.get("public/img/" + car.getImages());
            try {
                Files.delete(imagePath);
            } catch (Exception e) {
                System.out.println("Exception: " + e.getMessage());
            }
            iCarService.delete(car);
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
        return "redirect:/car-owner/my-cars";
    }

    @GetMapping("/edit")
    public String editCarDetails(@RequestParam long id, Model model, Principal principal) {
        String email = principal.getName();
        Account emailAccount = accountRepository.findByEmail(email);

        if (emailAccount == null) {
            return "redirect:/login";
        }

        Car car = carRepository.findById(id).orElse(null);
        if (car == null || !car.getCarOwner().getAccount().equals(emailAccount)) {
            return "redirect:/car-owner";
        }

        // Get the count of confirmed bookings for this car
        List<CarBooking> carBookings = carBookingRepository.findByCarId(car.getId());
        long confirmedBookingCount = carBookings.stream()
                .filter(cb -> "confirmed".equalsIgnoreCase(cb.getBooking().getStatus()))
                .count();

        model.addAttribute("car", car);
        model.addAttribute("confirmedRidesCount", confirmedBookingCount);
        setUpUserRole(model);  // Assuming this adds role-based attributes to the model
        model.addAttribute("currentPage", "edit-car");
        return "car-owner/edit-car2";
    }

    @GetMapping("/edit/{id}")
    public String editCarDetails(@PathVariable Long id, Model model, Principal principal) {
        String email = principal.getName();
        Account emailAccount = accountRepository.findByEmail(email);

        if (emailAccount == null) {
            return "redirect:/login";
        }

        Car car = carRepository.findById(id).orElse(null);
        if (car == null || !car.getCarOwner().getAccount().equals(emailAccount)) {
            return "redirect:/car-owner";
        }

        model.addAttribute("car", car);
        setUpUserRole(model);
        model.addAttribute("currentPage", "edit-car");
        return "car-owner/edit-car2";
    }

    @PostMapping("/edit/{id}")
    public String updateCarDetails(@PathVariable Long id, @ModelAttribute Car updatedCar, Principal principal) {
        String email = principal.getName();
        Account emailAccount = accountRepository.findByEmail(email);

        if (emailAccount == null) {
            return "redirect:/login";
        }

        Car existingCar = carRepository.findById(id).orElse(null);
        if (existingCar == null || !existingCar.getCarOwner().getAccount().equals(emailAccount)) {
            return "redirect:/car-owner/my-cars";
        }

        // Update car details
        existingCar.setLicensePlate(updatedCar.getLicensePlate());
        existingCar.setBrand(updatedCar.getBrand());
        existingCar.setModel(updatedCar.getModel());
        existingCar.setProductionYears(updatedCar.getProductionYears());
        existingCar.setTransmissionType(updatedCar.getTransmissionType());
        existingCar.setFuelType(updatedCar.getFuelType());
        existingCar.setNumbersOfSeats(updatedCar.getNumbersOfSeats());
        existingCar.setMileage(updatedCar.getMileage());
        existingCar.setFuelConsumption(updatedCar.getFuelConsumption());
        existingCar.setColor(updatedCar.getColor());
        existingCar.setDescription(updatedCar.getDescription());
        existingCar.setAdditionalFunctions(updatedCar.getAdditionalFunctions());
        existingCar.setBasicPrice(updatedCar.getBasicPrice());
        existingCar.setDeposit(updatedCar.getDeposit());
        existingCar.setTermsOfUse(updatedCar.getTermsOfUse());
        existingCar.setAddress(updatedCar.getAddress());
        existingCar.setName(updatedCar.getName());

        carRepository.save(existingCar);

        return "redirect:/car-owner/my-cars";
    }
}
