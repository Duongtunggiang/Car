package com.group.car.controller;

import com.group.car.models.*;
import com.group.car.repository.*;
import com.group.car.service.BookingService;
import com.group.car.service.CarServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/booking_old")
public class BookingController {
    @Autowired
    private CarOwnerRepository carOwnerRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private CarServiceImpl carService;

    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private CarRepository carRepository;
    @Autowired
    private CarBookingRepository carBookingRepository;
    @Autowired
    private BookingService bookingService;

    //Rent a car -- Duong
    private Customer getCurrentCustomer() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        String email = authentication.getName();
        return customerRepository.findByEmail(email).orElse(null);
    }

    @GetMapping("/rent-a-car")
    public String showRentalCarForm(@RequestParam("id") long id,
                                    Model model,
                                    Principal principal,
                                    HttpServletRequest request) {
        Car car = carRepository.findById(id).orElse(null);
        if (car == null) {
            model.addAttribute("message", "Car not found");
            return "car-not-found";
        }
        String email = principal.getName();
        Account emailAccount = accountRepository.findByEmail(email);
        if (emailAccount == null) {
            return "redirect:/login";
        }
        if (emailAccount.getRoles().stream().anyMatch(role -> role.getName().equals("Customer"))) {
            Customer customer = customerRepository.findByAccountId(emailAccount.getId());
            model.addAttribute("account", emailAccount);
            model.addAttribute("customer", customer);
        }

        Booking booking = new Booking();
        Customer customer = getCurrentCustomer();

        // Retrieve the search parameters from the session
        HttpSession session = request.getSession();
        String pickupLocation = (String) session.getAttribute("pickupLocation");
        LocalDate pickupDate = (LocalDate) session.getAttribute("pickupDate");
        LocalTime pickupTime = (LocalTime) session.getAttribute("pickupTime");
        LocalDate dropoffDate = (LocalDate) session.getAttribute("dropoffDate");
        LocalTime dropoffTime = (LocalTime) session.getAttribute("dropoffTime");

        // Combine LocalDate and LocalTime into LocalDateTime
        LocalDateTime pickupDateTime = LocalDateTime.of(pickupDate, pickupTime);
        LocalDateTime returnDateTime = LocalDateTime.of(dropoffDate, dropoffTime);

        model.addAttribute("car", car);
        model.addAttribute("pickupLocation", pickupLocation);
        model.addAttribute("pickupDateTime", pickupDateTime);
        model.addAttribute("returnDateTime", returnDateTime);
        setUpUserRole(model);
        return "customer/car-rental-form";
    }


    @GetMapping("/upload")
    public String uploadProfile(Model model) {


        return "customer/car-rental-form";
    }


    @PostMapping("/create-booking")
    public String createBooking(@RequestParam("carId") Long carId,
                                @RequestParam("pickUpLocation") String pickUpLocation,
                                @RequestParam("startDateTime") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") Date startDateTime,
                                @RequestParam("endDateTime") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") Date endDateTime,
                                @RequestParam(value = "renterName", required = false) String renterName,
                                @RequestParam(value = "renterDateOfBirth", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date renterDateOfBirth,
                                @RequestParam(value = "renterPhoneNo", required = false) String renterPhoneNo,
                                @RequestParam(value = "renterEmail", required = false) String renterEmail,
                                @RequestParam(value = "renterAddress", required = false) String renterAddress,
                                @RequestParam(value = "renterDrivingLicense", required = false) String renterDrivingLicense,
                                @RequestParam(value = "renterNationalId", required = false) String renterNationalId,
                                @RequestParam(value = "renterCity", required = false) String renterCity,
                                @RequestParam(value = "renterDistrict", required = false) String renterDistrict,
                                @RequestParam(value = "renterWard", required = false) String renterWard,
                                Principal principal) {

        // Retrieve the car and customer based on provided IDs
        Car car = carRepository.findById(carId).orElseThrow(() -> new RuntimeException("Car not found"));

        String email = principal.getName();
        Account emailAccount = accountRepository.findByEmail(email);

        if (emailAccount == null) {
            return "redirect:/login";
        }

        Customer customer = customerRepository.findByAccountId(emailAccount.getId());
        if (customer == null) {
            // Handle the case where Customer profile does not exist
            return "redirect:/profile/edit"; // Redirect to profile edit page if necessary
        }

        // Update Customer profile if new information is provided
        if (renterName != null && !renterName.isEmpty()) {
            customer.setName(renterName);
        }
        if (renterDateOfBirth != null) {
            customer.setDateOfBirth(renterDateOfBirth);
        }
        if (renterPhoneNo != null && !renterPhoneNo.isEmpty()) {
            customer.setPhoneNo(renterPhoneNo);
        }
        if (renterEmail != null && !renterEmail.isEmpty()) {
            customer.setEmail(renterEmail);
        }
        if (renterAddress != null && !renterAddress.isEmpty()) {
            customer.setAddress(renterAddress);
        }
        if (renterDrivingLicense != null && !renterDrivingLicense.isEmpty()) {
            customer.setDrivingLicense(renterDrivingLicense);
        }
        if (renterNationalId != null && !renterNationalId.isEmpty()) {
            customer.setNationalIdNo(renterNationalId);
        }
        // Assuming customerAddress related fields need to be updated
        // if not update customerAddress related fields based on your application logic

        customerRepository.save(customer);

        // Create a Booking
        Booking booking = new Booking();
        booking.setPickUpLocation(pickUpLocation);
        booking.setStartDateTime(startDateTime);
        booking.setEndDateTime(endDateTime);
        booking.setCustomer(customer);
        bookingRepository.save(booking);

        // Create a CarBooking to link Car and Booking
        CarBooking carBooking = new CarBooking();
        carBooking.setCar(car);
        carBooking.setBooking(booking);
        carBookingRepository.save(carBooking);

        return "redirect:/booking/summary";
    }


//
//    @PostMapping("/upload")
//    public String saveDriverInfo(@RequestParam String renterName,
//                                 @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date renterDateOfBirth,
//                                 @RequestParam String renterPhoneNo,
//                                 @RequestParam String renterEmail,
//                                 @RequestParam String renterNationalId,
//                                 @RequestParam String renterDrivingLicense,
//                                 @RequestParam String renterAddress,
//                                 Principal principal) {
//
//        // Lấy tài khoản đang đăng nhập
//        String email = principal.getName();
//        Account emailAccount = accountRepository.findByEmail(email);
//        if (emailAccount == null) {
//            return "redirect:/login";
//        }
//
//        // Lấy thông tin Customer từ Account và cập nhật thông tin
//        if (emailAccount.getRoles().stream().anyMatch(role -> role.getName().equals("Customer"))) {
//            Customer customer = customerRepository.findByAccountId(emailAccount.getId());
//            if (customer != null) {
//                customer.setName(renterName);
//                customer.setDateOfBirth(renterDateOfBirth);
//                customer.setPhoneNo(renterPhoneNo);
//                customer.setNationalIdNo(renterNationalId);
//                customer.setDrivingLicense(renterDrivingLicense);
//                customer.setAddress(renterAddress);
//                customerRepository.save(customer);
//            }
//        }
//
//        return "redirect:/profile";
//    }


    @PostMapping("/rent-a-car")
    public String rentCar(@ModelAttribute Booking booking, @RequestParam("carId") long carId, Model model) {
        try {
            Car car = carRepository.findById(carId).orElse(null);
            if (car == null) {
                model.addAttribute("error", "Car not found");
                return "customer/car-rental-form";
            }

            // Set up the booking with current user
            Customer customer = getCurrentCustomer();
            booking.setCustomer(customer);
            booking.setStatus("Pending"); // or any status you prefer

            // Save booking
            Booking savedBooking = bookingRepository.save(booking);

            // Create and save CarBooking
            CarBooking carBooking = new CarBooking();
            carBooking.setCar(car);
            carBooking.setBooking(savedBooking);
            carBookingRepository.save(carBooking);

            model.addAttribute("message", "Car booked successfully!");
            return "redirect:/booking/confirmation";
        } catch (Exception e) {
            model.addAttribute("error", "An error occurred while booking the car. Please try again.");
            return "customer/car-rental-form";
        }
    }


    @PostMapping("/rent")
    public String rentCard(@ModelAttribute Booking booking, @RequestParam("carIds") List<Long> carIds, Model model) {
        List<Car> cars = carRepository.findAllById(carIds);
        if (!cars.isEmpty()) {
            for (Car car : cars) {
                CarBooking bookingCar = new CarBooking();
                bookingCar.setBooking(booking);
                bookingCar.setCar(car);
                booking.getCarBookings().add(bookingCar);
            }
            bookingRepository.save(booking);
            return "redirect:/booking/confirmation";
        } else {
            model.addAttribute("error", "No cars found for booking");
            return "booking";
        }
    }

    @PostMapping("/update-booking")
    public String updateBookings(@RequestParam Long bookingId,
                                 @RequestParam Long carId,
                                 @RequestParam String pickUpLocation,
                                 @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") Date startDateTime,
                                 @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") Date endDateTime,
                                 RedirectAttributes redirectAttributes) {
        try {
            Booking booking = bookingRepository.findById(bookingId).orElseThrow();
            booking.setPickUpLocation(pickUpLocation);
            booking.setStartDateTime(startDateTime);
            booking.setEndDateTime(endDateTime);
            bookingRepository.save(booking);

            redirectAttributes.addFlashAttribute("message", "Booking details updated successfully.");
            return "redirect:/booking/change-details/" + carId + "?bookingId=" + bookingId;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Failed to update booking details.");
            return "redirect:/booking/change-details/" + carId + "?bookingId=" + bookingId;
        }
    }

//    @GetMapping("/booking/change-details/{bookingId}")
//    public String changeBookingDetails(@PathVariable Long bookingId, Model model) {
//        Booking booking = bookingService.findBookingById(bookingId);
//        if (booking == null) {
//            model.addAttribute("message", "Booking not found.");
//            return "redirect:/errorPage";
//        }
//
//        model.addAttribute("booking", booking);
//        return "booking/change-details";
//    }

//    @GetMapping("/change-details")
//    public String showChange(){
//        return "customer/change-details";
//    }

    //
    @GetMapping("/change-details")
    public String showChangeDetailsForm(@RequestParam("bookingId") Long bookingId,
                                        @PathVariable("carId") Long carId,
                                        Model model) {
//        Booking booking = bookingRepository.findById(bookingId).orElse(null);
        Booking booking = bookingService.findBookingById(bookingId);
        Car car = carRepository.findById(carId).orElse(null);

        if (booking == null || car == null) {
            model.addAttribute("message", "Booking or Car not found");
            return "error-page";
        }

        model.addAttribute("booking", booking);
        model.addAttribute("car", car);
        return "customer/change-details";
    }

//    @GetMapping("/booking-details")
//    public String viewBookingDetails(Model model) {
//        Booking booking = bookingRepository.findById(1L).orElse(null);
//        if (booking != null) {
//            model.addAttribute("booking", booking);
//        } else {
//            model.addAttribute("error", "Booking not found.");
//        }
//        setUpUserRole(model);
//        model.addAttribute("currentPage", "car-rental-form");
//        return "customer/car-rental-form";
//    }

//    @PostMapping("/rent-a-car")
//    public String saveBooking(@RequestParam(required = false, defaultValue = "0") long bookingId,
//                              @RequestParam long carId,
//                              @RequestParam String pickUpLocation,
//                              @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") Date startDateTime,
//                              @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") Date endDateTime,
//                              BindingResult result,
//                              Model model) {
//        if (result.hasErrors()) {
//            model.addAttribute("errorMessage", "Dữ liệu không hợp lệ!");
//            return "redirect:/error-page";
//        }
//
//        Booking booking = bookingId == 0 ? new Booking() : bookingRepository.findById(bookingId).orElse(new Booking());
//        booking.setPickUpLocation(pickUpLocation);
//        booking.setStartDateTime(startDateTime);
//        booking.setEndDateTime(endDateTime);
//        bookingRepository.save(booking);
//
//        CarBooking carBooking = new CarBooking();
//        carBooking.setCar(carRepository.findById(carId).orElseThrow());
//        carBooking.setBooking(booking);
//        carBookingRepository.save(carBooking);
//
//        return "redirect:/booking/confirmation";
//    }

    @PostMapping("/create-bookings")
    public String createBookings(@RequestParam("carId") Long carId,
                                 @RequestParam("pickUpLocation") String pickUpLocation,
                                 @RequestParam("startDateTime") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") Date startDateTime,
                                 @RequestParam("endDateTime") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") Date endDateTime,

//                                @RequestParam LocalDateTime startDateTime,
//                                @RequestParam LocalDateTime endDateTime,
                                 Model model) {
        // Lấy xe từ carId
        Car car = carService.findById(carId);
        if (car == null) {
            model.addAttribute("message", "Car not found");
            return "error";
        }

        Booking booking = new Booking();
//        booking.setCar(car);
        booking.setPickUpLocation(pickUpLocation);
        booking.setStartDateTime(startDateTime);
        booking.setEndDateTime(endDateTime);
        bookingService.saveBooking(booking);

        CarBooking carBooking = new CarBooking();
        carBooking.setCar(carRepository.findById(carId).orElseThrow());
        carBooking.setBooking(booking);
        carBookingRepository.save(carBooking);

        model.addAttribute("message", "Booking created successfully");
        return "redirect:/booking/success";
    }


    private void setUpUserRole(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof UserDetails userDetails) {
            String username = userDetails.getUsername();

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


//    @PostMapping("/submit-booking")
//    public String submitBooking(@ModelAttribute BookingFormDTO bookingFormDTO) {
//        Booking booking = new Booking();
//        booking.setRenterName(bookingFormDTO.getRenterName());
//        booking.setRenterDateOfBirth(bookingFormDTO.getRenterDateOfBirth());
//        booking.setRenterPhoneNo(bookingFormDTO.getRenterPhoneNo());
//        booking.setRenterEmail(bookingFormDTO.getRenterEmail());
//        booking.setRenterAddress(bookingFormDTO.getRenterAddress());
//        booking.setRenterDrivingLicense(bookingFormDTO.getRenterDrivingLicense());
//
//        bookingRepository.save(booking);
//
//        return "redirect:/success";
//    }
}