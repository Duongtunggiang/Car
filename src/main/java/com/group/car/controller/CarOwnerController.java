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
@RequestMapping("/carowner")
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

    @GetMapping({"", "/"})
    public String showAllCar(Model model, Principal principal) {
        String email = principal.getName();

        Optional<CarOwner> carOwnerOptional = carOwnerRepository.findByEmail(email);

        if (carOwnerOptional.isPresent()) {
            CarOwner carOwner = carOwnerOptional.get();
            List<Car> cars = carService.findAllByCarOwner(carOwner.getId());

            Map<Long, String> carStatusMap = new HashMap<>();

            for (Car car : cars) {
                List<CarBooking> carBookings = carBookingRepository.findByCarId(car.getId());
                if (!carBookings.isEmpty()) {
                    String status = carBookings.get(0).getBooking().getStatus();
                    carStatusMap.put(car.getId(), status);
                } else {
                    carStatusMap.put(car.getId(), "No Booking");
                }
            }

            model.addAttribute("cars", cars);
            model.addAttribute("carStatusMap", carStatusMap);

        } else {
            model.addAttribute("error", "No cars found for this user.");
        }

        setUpUserRole(model);
        model.addAttribute("currentPage", "car");
        return "car-owner/car";
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

        Booking booking = new Booking();
        booking.setStatus("Available");
        booking.setPickUpLocation(car.getAddress());
        booking.setBookingNo(UUID.randomUUID().toString());
        bookingRepository.save(booking);

        CarBooking carBooking = new CarBooking();
        carBooking.setCar(car);
        carBooking.setBooking(booking);
        carBookingRepository.save(carBooking);

        return "redirect:/carowner";
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

        return "redirect:/carowner";
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
        return "redirect:/carowner";
    }

    @GetMapping("/edit")
    public String showEditCar(Model model, @RequestParam long id) {
        try {
            Car car = iCarService.findById(id).get();
            model.addAttribute("car", car);

            CarDto carDto = new CarDto();
            // Mapping fields from car to carDto
            carDto.setName(car.getName());
            carDto.setLicensePlate(car.getLicensePlate());
            carDto.setBrand(car.getBrand());
            carDto.setModel(car.getModel());
            carDto.setColor(car.getColor());
            carDto.setNumbersOfSeats(car.getNumbersOfSeats());
            carDto.setProductionYears(car.getProductionYears());
            carDto.setTransmissionType(car.getTransmissionType());
            carDto.setFuelType(car.getFuelType());
            carDto.setMileage(car.getMileage());
            carDto.setFuelConsumption(car.getFuelConsumption());
            carDto.setBasicPrice(car.getBasicPrice());
            carDto.setDeposit(car.getDeposit());
            carDto.setAddress(car.getAddress());
            carDto.setDescription(car.getDescription());
            carDto.setAdditionalFunction(car.getAdditionalFunctions());
            carDto.setTermsOfUse(car.getTermsOfUse());

            model.addAttribute("carDto", carDto);

        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
            return "redirect:/carowner";
        }
        setUpUserRole(model);
        model.addAttribute("currentPage", "edit");

        return "car-owner/edit";
    }

    @PostMapping("/edit")
    public String updateCar(Model model, @RequestParam long id,
                            @Valid @ModelAttribute CarDto carDto, BindingResult result,
                            Principal principal) {
        try {
            Car car = iCarService.findById(id).get();
            CarOwner carOwner = carOwnerRepository.findByAccountEmail(principal.getName());
            if (result.hasErrors()) {
                model.addAttribute("car", car);
                return "car-owner/edit";
            }

            if (!carDto.getImages().isEmpty()) {
                String uploadDir = "public/img/";
                Path oldImagePath = Paths.get(uploadDir + car.getImages());
                try {
                    Files.delete(oldImagePath);
                } catch (Exception e) {
                    System.out.println("Exception: " + e.getMessage());
                }

                MultipartFile image = carDto.getImages();
                String storageFileName = image.getOriginalFilename();

                try (InputStream inputStream = image.getInputStream()) {
                    Files.copy(inputStream, Paths.get(uploadDir + storageFileName), StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    System.out.println("Exception: " + e.getMessage());
                }
                car.setImages(storageFileName);
            }

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

            car.setCarOwner(carOwner);

            iCarService.save(car);

        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
        return "redirect:/carowner";
    }
}
