package com.group.car.controller;

import com.group.car.models.*;
import com.group.car.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.temporal.ChronoUnit;
import java.util.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
        setUpUserRole(model);
        model.addAttribute("currentPage", "home");
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
    @PostMapping("/my-bookings/{id}/submit-feedback")
    public String submitFeedback(@PathVariable Long id, @RequestParam("rating") int rating, @RequestParam("review") String review, Principal principal, RedirectAttributes redirectAttributes) {
        String email = principal.getName();
        Account emailAccount = accountRepository.findByEmail(email);

        if (emailAccount == null) {
            redirectAttributes.addFlashAttribute("error", "User not found");
            return "redirect:/customer/my-bookings";
        }

        Booking booking = bookingRepository.findById(id).orElse(null);
        if (booking == null || !booking.getStatus().equals("Completed")) {
            redirectAttributes.addFlashAttribute("error", "Invalid booking or status");
            return "redirect:/customer/my-bookings";
        }

        Feedback feedback = new Feedback();
        feedback.setRatings(rating);
        feedback.setContent(review);
        feedback.setBooking(booking);
        feedback.setDateTime(new Date());
        feedbackRepository.save(feedback);

        redirectAttributes.addFlashAttribute("success", "Feedback submitted successfully");
        return "redirect:/customer/my-bookings";
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

    //    @GetMapping("/car-details")
//    public String showCarDetails(@RequestParam("id") long id, Model model) {
//        Car car = carRepository.findById(id).orElse(null);
//        if (car == null) {
//            model.addAttribute("message", "Car not found");
//            return "car-not-found";
//        }
//        model.addAttribute("car", car);
//        setUpUserRole(model);
//        model.addAttribute("currentPage", "car-details");
//        return "customer/car-details";
//    }
    private void setUpUserRole(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
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


    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    CarOwnerRepository carOwnerRepository;

    @Autowired
    CarBookingRepository carBookingRepository;

    @GetMapping("/my-bookings")
    public String viewMyBookings(Model model, Principal principal) {
        String email = principal.getName();
        Account emailAccount = accountRepository.findByEmail(email);

        if (emailAccount == null) {
            return "redirect:/login";
        }

        if (emailAccount.getRoles().stream().anyMatch(role -> role.getName().equals("Customer"))) {
            Customer customer = customerRepository.findByAccountId(emailAccount.getId());
            List<Booking> bookings = customer.getBookings();

            bookings.sort((b1, b2) -> Long.compare(b2.getId(), b1.getId()));

            model.addAttribute("bookings", bookings);
        } else {
            model.addAttribute("bookings", null);
        }
        setUpUserRole(model);
        model.addAttribute("currentPage", "my-bookings");
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

        Customer customer = booking.getCustomer();
        model.addAttribute("booking", booking);
        model.addAttribute("customer", customer);

        setUpUserRole(model);
        model.addAttribute("currentPage", "detail-a-booking-example");
        return "customer/detail-a-booking-example";
    }
    @PostMapping("/my-bookings/{id}/confirm-pickup")
    public String confirmPickup(@PathVariable Long id, Model model, Principal principal) {
        String email = principal.getName();
        Account emailAccount = accountRepository.findByEmail(email);

        if (emailAccount == null) {
            return "redirect:/login";
        }

        // Lấy danh sách các CarBooking cho bookingId
        List<CarBooking> carBookings = carBookingRepository.findByBookingId(id);
        if (carBookings == null || carBookings.isEmpty()) {
            return "redirect:/customer/my-bookings";
        }

        CarBooking carBooking = carBookings.get(0);
        Booking booking = carBooking.getBooking();
        Car car = carBooking.getCar();
        if (booking == null || car == null) {
            return "redirect:/customer/my-bookings";
        }

        // Check if booking status allows confirmation
        if (booking.getStatus().equals("Booked")) {
            booking.setStatus("In-Progress");
            bookingRepository.save(booking);

            Customer customer = booking.getCustomer();
            CarOwner carOwner = car.getCarOwner();

            int deposit = car.getDeposit();
            int basicPrice = car.getBasicPrice();

            // Calculate the amount to pay the car owner
            int remainingAmount = basicPrice - deposit;

            // Ensure the customer has sufficient funds for the remaining amount
            if (customer.getWallet() < remainingAmount) {
                model.addAttribute("error", "Insufficient funds in wallet");
                return "redirect:/customer/my-bookings/" + id;
            }

            // Subtract the remaining amount from the customer's wallet
            customer.setWallet(customer.getWallet() - remainingAmount);
            customerRepository.save(customer);

            // Add the remaining amount to the car owner's wallet
            carOwner.setWallet(carOwner.getWallet() + remainingAmount);
            carOwnerRepository.save(carOwner);

            // Save the transaction for the customer (pay remaining amount)
            Transaction customerTransaction = new Transaction();
            customerTransaction.setAmount(remainingAmount);
            customerTransaction.setType("Offset final payment");
            customerTransaction.setCustomer(customer);
            customerTransaction.setCarName(car.getName());
            customerTransaction.setBookingNo(booking.getBookingNo());
            customerTransaction.setTransactionDateTime(new Date());
            customerTransaction.setWalletBalance(customer.getWallet()); // Updated balance
            transactionRepository.save(customerTransaction);

            // Save the transaction for the car owner (receive remaining amount)
            Transaction ownerTransaction = new Transaction();
            ownerTransaction.setAmount(remainingAmount);
            ownerTransaction.setType("Receive remaining amount");
            ownerTransaction.setCarOwner(carOwner);
            ownerTransaction.setCarName(car.getName());
            ownerTransaction.setBookingNo(booking.getBookingNo());
            ownerTransaction.setTransactionDateTime(new Date());
            ownerTransaction.setWalletBalance(carOwner.getWallet()); // Updated balance
            transactionRepository.save(ownerTransaction);
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

        List<CarBooking> carBookings = carBookingRepository.findByBookingId(id);

        if (carBookings == null || carBookings.isEmpty()) {
            return "redirect:/customer/my-bookings";
        }

        CarBooking carBooking = carBookings.get(0);
        Booking booking = carBooking.getBooking();
        Car car = carBooking.getCar();
        Customer customer = booking.getCustomer();
        CarOwner carOwner = car.getCarOwner();
        int deposit = car.getDeposit();

        // Tính số tiền bị trừ (một nửa số tiền cọc)
        int penaltyAmount = deposit / 2;

        // Trừ một nửa số tiền cọc từ ví của khách hàng
        customer.setWallet(customer.getWallet() + penaltyAmount);
        customerRepository.save(customer);

        // Cộng số tiền bị trừ vào ví của chủ xe
        carOwner.setWallet(carOwner.getWallet() - penaltyAmount);
        carOwnerRepository.save(carOwner);

        // Lưu lịch sử giao dịch của khách hàng (trả lại một nửa số tiền cọc)
        Transaction customerTransaction = new Transaction();
        customerTransaction.setAmount(penaltyAmount);
        customerTransaction.setType("Refund deposit");
        customerTransaction.setCustomer(customer);
        customerTransaction.setTransactionDateTime(new Date());
        customerTransaction.setWalletBalance(customer.getWallet()); // Cập nhật số dư
        transactionRepository.save(customerTransaction);

        // Lưu lịch sử giao dịch của chủ xe (nhận một nửa số tiền cọc)
        Transaction ownerTransaction = new Transaction();
        ownerTransaction.setAmount(penaltyAmount);
        ownerTransaction.setType("Receive penalty deposit");
        ownerTransaction.setCarOwner(carOwner);
        ownerTransaction.setTransactionDateTime(new Date());
        ownerTransaction.setWalletBalance(carOwner.getWallet()); // Cập nhật số dư
        transactionRepository.save(ownerTransaction);

        // Cập nhật trạng thái đặt xe thành "Cancelled"
        booking.setStatus("Cancelled");
        bookingRepository.save(booking);

        return "redirect:/customer/my-bookings/" + id;
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



}