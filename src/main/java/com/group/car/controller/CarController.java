package com.group.car.controller;

import com.group.car.models.*;
import com.group.car.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Controller()
@RequestMapping("/customer")
public class CarController {
    @Autowired
    private CarRepository carRepository;

    @Autowired
    private FeedbackRepository feedbackRepository;

    @GetMapping("/")
    public String homeCustomer(Model model){
        model.addAttribute("");
        List<Car> cars = carRepository.findAll();
        model.addAttribute("cars", cars);
        return "home";
    }

    @PostMapping("/my-bookings/{id}/confirm-return")
    @ResponseBody
    public Map<String, Object> confirmReturnCar(@PathVariable Long id, Principal principal) {
        Map<String, Object> response = new HashMap<>();

        String email = principal.getName();
        Account emailAccount = accountRepository.findByEmail(email);

        if (emailAccount == null) {
            response.put("success", false);
            response.put("message", "User not found");
            return response;
        }

        Booking booking = bookingRepository.findById(id).orElse(null);
        if (booking == null || !booking.getStatus().equals("In-Progress")) {
            response.put("success", false);
            response.put("message", "Invalid booking or status");
            return response;
        }

        double totalAmount = calculateTotalAmount(booking);
        double deposit = booking.getCarBookings().get(0).getCar().getDeposit();
        double difference = totalAmount - deposit;

        // Update user's wallet balance
        Customer customer = booking.getCustomer();
        if (difference > 0) {
            if (customer.getWallet() < difference) {
                booking.setStatus("Pending Payment");
                bookingRepository.save(booking);
                response.put("success", false);
                response.put("message", "Insufficient funds in wallet");
                return response;
            }
            customer.setWallet((int) (customer.getWallet() - difference));
        } else {
            customer.setWallet((int) (customer.getWallet() + Math.abs(difference)));
        }
        customerRepository.save(customer);

        // Update booking status
        booking.setStatus("Completed");
        bookingRepository.save(booking);

        response.put("success", true);
        response.put("message", "Car returned successfully");
        return response;
    }


    @PostMapping("/my-bookings/{id}/continue-payment")
    @ResponseBody
    public Map<String, Object> continuePayment(@PathVariable Long id, Principal principal) {
        Map<String, Object> response = new HashMap<>();

        String email = principal.getName();
        Account emailAccount = accountRepository.findByEmail(email);

        if (emailAccount == null) {
            response.put("success", false);
            response.put("message", "User not found");
            return response;
        }

        Booking booking = bookingRepository.findById(id).orElse(null);
        if (booking == null || !booking.getStatus().equals("Pending Payment")) {
            response.put("success", false);
            response.put("message", "Invalid booking or status");
            return response;
        }

        Customer customer = booking.getCustomer();
        double totalAmount = calculateTotalAmount(booking);
        double deposit = booking.getCarBookings().get(0).getCar().getDeposit();
        double difference = totalAmount - deposit;

        if (customer.getWallet() < difference) {
            response.put("success", false);
            response.put("message", "Insufficient funds in wallet");
            return response;
        }

        customer.setWallet((int) (customer.getWallet() - difference));
        customerRepository.save(customer);

        booking.setStatus("Completed");
        bookingRepository.save(booking);

        response.put("success", true);
        response.put("message", "Payment completed successfully");
        return response;
    }



    private double calculateTotalAmount(Booking booking) {
        return booking.getCarBookings().get(0).getCar().getBasicPrice() *
                ChronoUnit.DAYS.between(booking.getStartDateTime().toInstant(), booking.getEndDateTime().toInstant());
    }


    @PostMapping("/my-bookings/{id}/rate")
    @ResponseBody
    public Map<String, Object> rateBooking(@PathVariable Long id, @RequestBody Map<String, Object> payload, Principal principal) {
        Map<String, Object> response = new HashMap<>();

        String email = principal.getName();
        Account emailAccount = accountRepository.findByEmail(email);

        if (emailAccount == null) {
            response.put("success", false);
            response.put("message", "User not found");
            return response;
        }

        Booking booking = bookingRepository.findById(id).orElse(null);
        if (booking == null || !booking.getStatus().equals("Completed")) {
            response.put("success", false);
            response.put("message", "Invalid booking or status");
            return response;
        }

        int rating = Integer.parseInt(payload.get("rating").toString());
        String review = (String) payload.get("review");

        Feedback feedback = new Feedback();
        feedback.setRatings(rating);
        feedback.setContent(review);
        feedback.setDateTime(new Date());
        feedback.setBooking(booking);
        feedbackRepository.save(feedback);


        response.put("success", true);
        response.put("message", "Rating submitted successfully");
        return response;
    }

    //    @GetMapping("/car-details/{id}")
//    public String showCarDetails(@PathVariable("id") Long id, Model model) {
//        Car car = carRepository.findById(id).orElse(null);
//        if (car == null) {
//            model.addAttribute("message", "No car found with the given ID");
//            return "car-details-empty";
//        }
//        model.addAttribute("car", car);
//        return "car-details";
//    }

    @GetMapping("/car-details")
    public String showCarDetails(@RequestParam("id") long id, Model model) {
        Car car = carRepository.findById(id).orElse(null);
        if (car == null) {
            model.addAttribute("message", "Car not found");
            return "car-not-found";
        }
        model.addAttribute("car", car);
        return "customer/car-details";
    }


    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @GetMapping("/my-bookings")
    public String viewMyBookings(Model model, Principal principal) {
        String email = principal.getName();
        Account emailAccount = accountRepository.findByEmail(email);

        if (emailAccount == null) {
            return "redirect:/login";
        }

        if (emailAccount.getRoles().stream().anyMatch(role -> role.getName().equals("Customer"))) {
            Customer customer = customerRepository.findByAccountId(emailAccount.getId());
            List<Booking> bookings = customer.getBookings();  // Ensure getBookings() is defined in Customer class
            model.addAttribute("bookings", bookings);
        } else {
            model.addAttribute("bookings", null);  // No bookings if not a customer
        }

        return "customer/my-bookings";
    }

    @GetMapping("/my-bookings/{id}")
    public String viewBookingDetails(@PathVariable Long id, Model model, Principal principal) {
        String email = principal.getName();
        Account emailAccount = accountRepository.findByEmail(email);

        if (emailAccount == null) {
            return "redirect:/login";
        }

        Booking booking = bookingRepository.findById(id).orElse(null);
        if (booking == null) {
            return "redirect:/customer/my-bookings";
        }

        Customer customer = booking.getCustomer(); // Assuming there's a Customer object in Booking
        model.addAttribute("booking", booking);
        model.addAttribute("customer", customer);
        return "customer/detail-a-booking-example";
    }

//    @PostMapping("/my-bookings/{bookingId}/update-renter-infor")
//    public String updateRenterInfo(@PathVariable Long bookingId, @ModelAttribute Customer updatedCustomer, Principal principal) {
//        String email = principal.getName();
//        Account emailAccount = accountRepository.findByEmail(email);
//
//        if (emailAccount == null) {
//            return "redirect:/login";
//        }
//
//        Booking booking = bookingRepository.findById(bookingId).orElse(null);
//        if (booking == null) {
//            return "redirect:/customer/my-bookings";
//        }
//
//        Customer existingCustomer = customerRepository.findById(updatedCustomer.getId()).orElse(null);
//        if (existingCustomer == null || !emailAccount.equals(existingCustomer.getAccount())) {
//            return "redirect:/login";
//        }
//
//        existingCustomer.setName(updatedCustomer.getName());
//        existingCustomer.setDateOfBirth(updatedCustomer.getDateOfBirth());
//        existingCustomer.setPhoneNo(updatedCustomer.getPhoneNo());
//        existingCustomer.setEmail(updatedCustomer.getEmail());
//        existingCustomer.setNationalIdNo(updatedCustomer.getNationalIdNo());
//        existingCustomer.setDrivingLicense(updatedCustomer.getDrivingLicense());
//        existingCustomer.setAddress(updatedCustomer.getAddress());
//
//        // Save the updated customer information
//        customerRepository.save(existingCustomer);
//
//        return "redirect:/customer/my-bookings/" + bookingId;
//    }


    @PostMapping("/my-bookings/{id}/confirm-pickup")
    public String confirmPickup(@PathVariable Long id, Model model, Principal principal) {
        String email = principal.getName();
        Account emailAccount = accountRepository.findByEmail(email);

        if (emailAccount == null) {
            return "redirect:/login";
        }

        Booking booking = bookingRepository.findById(id).orElse(null);
        if (booking == null) {
            return "redirect:/customer/my-bookings";
        }

        // Check if booking status allows confirmation
        if (booking.getStatus().equals("Confirmed")) {
            booking.setStatus("In-Progress");
            bookingRepository.save(booking);
        }

        return "redirect:/customer/my-bookings/" + id;
    }


    @PostMapping("/my-bookings/{id}/cancel")
    public String cancelBooking(@PathVariable Long id, Model model, Principal principal) {
        String email = principal.getName();
        Account emailAccount = accountRepository.findByEmail(email);

        if (emailAccount == null) {
            return "redirect:/login";
        }

        Booking booking = bookingRepository.findById(id).orElse(null);
        if (booking == null) {
            return "redirect:/customer/my-bookings";
        }

        // Update booking status to "Cancelled"
        booking.setStatus("Cancelled");
        bookingRepository.save(booking);

        return "redirect:/customer/my-bookings/" + id;
    }
}