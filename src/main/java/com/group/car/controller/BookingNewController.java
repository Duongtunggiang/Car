package com.group.car.controller;

import com.group.car.models.*;
import com.group.car.repository.BookingRepository;
import com.group.car.repository.CarRepository;
import com.group.car.repository.CustomerRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/booking")
public class BookingNewController {

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @GetMapping("/rent-a-car")
    public String showRentalCarForm(@RequestParam("id") long carId, Model model, Principal principal, HttpServletRequest request) {
        Optional<Car> carOptional = carRepository.findById(carId);
        if (!carOptional.isPresent()) {
            return "redirect:/car-not-found";
        }
        Car car = carOptional.get();

        Customer customer = getCurrentCustomer();
        if (customer == null) {
            return "redirect:/login";
        }

        Booking booking = new Booking();
        booking.setCustomer(customer);

        HttpSession session = request.getSession();
        String pickupLocation = (String) session.getAttribute("pickupLocation");
        LocalDate pickupDate = (LocalDate) session.getAttribute("pickupDate");
        LocalTime pickupTime = (LocalTime) session.getAttribute("pickupTime");
        LocalDate dropoffDate = (LocalDate) session.getAttribute("dropoffDate");
        LocalTime dropoffTime = (LocalTime) session.getAttribute("dropoffTime");

        // Combine LocalDate and LocalTime into LocalDateTime
        LocalDateTime pickupDateTime = LocalDateTime.of(pickupDate, pickupTime);
        LocalDateTime returnDateTime = LocalDateTime.of(dropoffDate, dropoffTime);

        booking.setPickUpLocation(pickupLocation);
        booking.setStartDateTime(Date.from(pickupDateTime.atZone(ZoneId.systemDefault()).toInstant()));
        booking.setEndDateTime(Date.from(returnDateTime.atZone(ZoneId.systemDefault()).toInstant()));

        // Calculate number of days
        long numberOfDays = ChronoUnit.DAYS.between(pickupDateTime.toLocalDate(), returnDateTime.toLocalDate());
        if (numberOfDays == 0) {
            numberOfDays = 1; // Minimum 1 day rental
        }
        booking.setNumberOfDays((int) numberOfDays);

        // Calculate total price
        int totalPrice = car.getBasicPrice() * (int) numberOfDays;
        booking.setTotalPrice(totalPrice);

        model.addAttribute("car", car);
        model.addAttribute("booking", booking);
        model.addAttribute("customer", customer);
        model.addAttribute("pickupDateTime", pickupDateTime);
        model.addAttribute("returnDateTime", returnDateTime);

        return "booking/step1-booking-information";
    }

    @PostMapping("/step1")
    public String processStep1(@ModelAttribute Booking booking, @RequestParam("carId") long carId, Model model, HttpSession session) {
        Optional<Car> carOptional = carRepository.findById(carId);
        if (!carOptional.isPresent()) {
            return "redirect:/car-not-found";
        }
        Car car = carOptional.get();

        Customer customer = getCurrentCustomer();
        if (customer == null) {
            return "redirect:/login";
        }

        String pickupLocation = (String) session.getAttribute("pickupLocation");
        LocalDate pickupDate = (LocalDate) session.getAttribute("pickupDate");
        LocalTime pickupTime = (LocalTime) session.getAttribute("pickupTime");
        LocalDate dropoffDate = (LocalDate) session.getAttribute("dropoffDate");
        LocalTime dropoffTime = (LocalTime) session.getAttribute("dropoffTime");

        // Combine LocalDate and LocalTime into LocalDateTime
        LocalDateTime pickupDateTime = LocalDateTime.of(pickupDate, pickupTime);
        LocalDateTime returnDateTime = LocalDateTime.of(dropoffDate, dropoffTime);

        booking.setPickUpLocation(pickupLocation);
        booking.setStartDateTime(Date.from(pickupDateTime.atZone(ZoneId.systemDefault()).toInstant()));
        booking.setEndDateTime(Date.from(returnDateTime.atZone(ZoneId.systemDefault()).toInstant()));

        // Calculate number of days
        long numberOfDays = ChronoUnit.DAYS.between(pickupDateTime.toLocalDate(), returnDateTime.toLocalDate());
        if (numberOfDays == 0) {
            numberOfDays = 1; // Minimum 1 day rental
        }
        booking.setNumberOfDays((int) numberOfDays);

        // Calculate total price
        int totalPrice = car.getBasicPrice() * (int) numberOfDays;
        booking.setTotalPrice(totalPrice);

        // Save the booking in the session for step 2
        session.setAttribute("booking", booking);

        model.addAttribute("booking", booking);
        model.addAttribute("car", car);
        model.addAttribute("customer", customer);

        return "booking/step2-payment";
    }

    @PostMapping("/step2")
    public String processStep2(@ModelAttribute Booking booking, @RequestParam("paymentMethod") String paymentMethod, @RequestParam("carId") long carId, Model model) {
        Optional<Car> carOptional = carRepository.findById(carId);
        if (!carOptional.isPresent()) {
            return "redirect:/car-not-found";
        }
        Car car = carOptional.get();

        Customer customer = getCurrentCustomer();
        if (customer == null) {
            return "redirect:/login";
        }

        booking.setPaymentMethod(paymentMethod);
        booking.setCustomer(customer);

        // Calculate deposit
        int deposit = car.getDeposit();

        // Check if the customer has sufficient funds if using wallet
        if (paymentMethod.equals("wallet") && customer.getWallet() < deposit) {
            model.addAttribute("error", "Insufficient funds in wallet");
            return "booking/step2-payment";
        }

        // Process the payment
        boolean paymentSuccess = processPayment(booking, customer, car);

        if (!paymentSuccess) {
            model.addAttribute("error", "Payment processing failed");
            return "booking/step2-payment";
        }

        // Generate a unique booking number
        String bookingNumber = generateBookingNumber();
        booking.setBookingNo(bookingNumber);

        // Set the status to "Confirmed"
        booking.setStatus("Confirmed");

        // Create and save CarBooking
        CarBooking carBooking = new CarBooking();
        carBooking.setCar(car);
        carBooking.setBooking(booking);
        booking.getCarBookings().add(carBooking);

        // Save the booking to the database
        bookingRepository.save(booking);

        return "redirect:/booking/step3/" + booking.getId();
    }

    @GetMapping("/step3/{bookingId}")
    public String showStep3(@PathVariable Long bookingId, Model model) {
        Optional<Booking> bookingOptional = bookingRepository.findById(bookingId);
        if (!bookingOptional.isPresent()) {
            return "redirect:/booking-not-found";
        }
        Booking booking = bookingOptional.get();
        Car car = booking.getCarBookings().get(0).getCar();

        model.addAttribute("booking", booking);
        model.addAttribute("car", car);
        model.addAttribute("customer", booking.getCustomer());

        return "booking/step3-finish";
    }

    private boolean processPayment(Booking booking, Customer customer, Car car) {
        int deposit = car.getDeposit();

        if (booking.getPaymentMethod().equals("wallet")) {
            if (customer.getWallet() >= deposit) {
                customer.setWallet(customer.getWallet() - deposit);
                customerRepository.save(customer);
                return true;
            }
        } else if (booking.getPaymentMethod().equals("cash") || booking.getPaymentMethod().equals("bankTransfer")) {
            // For cash and bank transfer, we assume the payment will be made later
            return true;
        }
        return false;
    }

    private String generateBookingNumber() {
        return UUID.randomUUID().toString();
    }

    private Customer getCurrentCustomer() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        String email = authentication.getName();
        return customerRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
    }
}
