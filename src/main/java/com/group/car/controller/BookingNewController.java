package com.group.car.controller;

import com.group.car.models.*;
import com.group.car.repository.*;
import com.group.car.service.CarService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
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
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Controller
@RequestMapping("/booking")
public class BookingNewController {

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private CarService carService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CarBookingRepository carBookingRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @GetMapping("/rent-a-car")
    public String showRentalCarForm(@RequestParam("id") long carId, Model model, Principal principal, HttpServletRequest request) {
        Optional<Car> carOptional = carRepository.findById(carId);
        if (!carOptional.isPresent()) {
            return "redirect:/car-not-found";
        }
        Car car = carOptional.get();

        if (principal == null) {
            return "redirect:/login";
        }

        String email = principal.getName();
        Account emailAccount = accountRepository.findByEmail(email);

        if (emailAccount == null) {
            return "redirect:/login";
        }

        Customer customer = getCurrentCustomer();
        if (customer == null) {
            throw new RuntimeException("Customer not found");
        }

        Booking booking = new Booking();
        booking.setCustomer(customer);
        HttpSession session = request.getSession();
        String pickupLocation = (String) car.getAddress();

        LocalDate pickupDate = (LocalDate) session.getAttribute("pickupDate");
        LocalTime pickupTime = (LocalTime) session.getAttribute("pickupTime");
        LocalDate dropoffDate = (LocalDate) session.getAttribute("dropoffDate");
        LocalTime dropoffTime = (LocalTime) session.getAttribute("dropoffTime");

        if (pickupDate == null) pickupDate = LocalDate.now();
        if (pickupTime == null) pickupTime = LocalTime.MIDNIGHT;
        if (dropoffDate == null) dropoffDate = LocalDate.now().plusDays(1);
        if (dropoffTime == null) dropoffTime = LocalTime.MIDNIGHT;

        LocalDateTime pickupDateTime = LocalDateTime.of(pickupDate, pickupTime);
        LocalDateTime returnDateTime = LocalDateTime.of(dropoffDate, dropoffTime);

        booking.setPickUpLocation(pickupLocation);
        booking.setStartDateTime(Date.from(pickupDateTime.atZone(ZoneId.systemDefault()).toInstant()));
        booking.setEndDateTime(Date.from(returnDateTime.atZone(ZoneId.systemDefault()).toInstant()));

        long numberOfDays = ChronoUnit.DAYS.between(pickupDateTime.toLocalDate(), returnDateTime.toLocalDate());
        if (numberOfDays == 0) {
            numberOfDays = 1; // Minimum 1 day rental
        }
        booking.setNumberOfDays((int) numberOfDays);

        int totalPrice = car.getBasicPrice() * (int) numberOfDays;
        booking.setTotalPrice(totalPrice);

        model.addAttribute("car", car);
        model.addAttribute("booking", booking);
        model.addAttribute("customer", customer);
        model.addAttribute("pickupDateTime", pickupDateTime);
        model.addAttribute("returnDateTime", returnDateTime);

        return "booking/step1-booking-information";
    }

    @PostMapping("/update-booking")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateBooking(
            @RequestParam("carId") long carId,
            @RequestParam("pickUpLocation") String pickUpLocation,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate pickupDate,
            @RequestParam @DateTimeFormat(pattern = "HH:mm") LocalTime pickupTime,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dropoffDate,
            @RequestParam @DateTimeFormat(pattern = "HH:mm") LocalTime dropoffTime,
            HttpSession session) {

        LocalDateTime startDateTime = LocalDateTime.of(pickupDate, pickupTime);
        LocalDateTime endDateTime = LocalDateTime.of(dropoffDate, dropoffTime);

        Date startDate = Date.from(startDateTime.atZone(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(endDateTime.atZone(ZoneId.systemDefault()).toInstant());

        Optional<Car> carOptional = carRepository.findById(carId);
        if (!carOptional.isPresent()) {
            return ResponseEntity.badRequest().body(null);
        }
        Car car = carOptional.get();

        long numberOfDays = ChronoUnit.DAYS.between(startDateTime.toLocalDate(), endDateTime.toLocalDate());
        if (numberOfDays == 0) {
            numberOfDays = 1;
        }

        int totalPrice = car.getBasicPrice() * (int) numberOfDays;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String pickupDateTimeFormatted = startDateTime.format(formatter);
        String returnDateTimeFormatted = endDateTime.format(formatter);

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("pickUpLocation", pickUpLocation);
        responseData.put("pickupDateTimeFormatted", pickupDateTimeFormatted);
        responseData.put("returnDateTimeFormatted", returnDateTimeFormatted);
        responseData.put("numberOfDays", numberOfDays);
        responseData.put("totalPrice", totalPrice);

        session.setAttribute("pickupLocation", pickUpLocation);
        session.setAttribute("pickupDate", pickupDate);
        session.setAttribute("pickupTime", pickupTime);
        session.setAttribute("dropoffDate", dropoffDate);
        session.setAttribute("dropoffTime", dropoffTime);

        return ResponseEntity.ok(responseData);
    }
    @Autowired
    CarOwnerRepository carOwnerRepository;

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

        String pickupLocation = (String) car.getAddress();
        LocalDate pickupDate = (LocalDate) session.getAttribute("pickupDate");
        LocalTime pickupTime = (LocalTime) session.getAttribute("pickupTime");
        LocalDate dropoffDate = (LocalDate) session.getAttribute("dropoffDate");
        LocalTime dropoffTime = (LocalTime) session.getAttribute("dropoffTime");

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

        List<CarBooking> carBookings = carBookingRepository.findByCarId(carId);
        String status = "Available";

        session.setAttribute("startDateTime", booking.getStartDateTime());
        session.setAttribute("endDateTime", booking.getEndDateTime());


        model.addAttribute("booking", booking);
        model.addAttribute("car", car);
        model.addAttribute("customer", customer);
        model.addAttribute("status", status);

        return "booking/step2-payment";
    }

    @PostMapping("/step2")
    public String processStep2(
            @RequestParam("paymentMethod") String paymentMethod,
            @RequestParam("carId") long carId,
            @RequestParam("startDateTime") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") Date startDateTime,
            @RequestParam("endDateTime") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") Date endDateTime,
            Model model) {

        Optional<Car> carOptional = carRepository.findById(carId);
        if (!carOptional.isPresent()) {
            return "redirect:/car-not-found";
        }
        Car car = carOptional.get();

        Customer customer = getCurrentCustomer();
        if (customer == null) {
            return "redirect:/login";
        }

        int deposit = car.getDeposit();

        if (paymentMethod.equals("wallet") && customer.getWallet() < deposit) {
            model.addAttribute("error", "Insufficient funds in wallet");
            return "booking/step2-payment";
        }

        String bookingNumber = generateBookingNumber();

        List<CarBooking> carBookings = carBookingRepository.findByCarId(car.getId());

        if (!carBookings.isEmpty()) {
            CarBooking carBooking = carBookings.get(0);
            Booking booking = carBooking.getBooking();
            booking.setBookingNo(bookingNumber);
            booking.setPickUpLocation(car.getAddress());
            booking.setStartDateTime(startDateTime);
            booking.setEndDateTime(endDateTime);
            booking.setPaymentMethod(paymentMethod);
            booking.setCustomer(customer);

            boolean paymentSuccess = processPayment(booking, customer, car);

            if (!paymentSuccess) {
                model.addAttribute("error", "Payment processing failed");
                return "booking/step2-payment";
            }

            booking.setStatus("Confirmed");
            bookingRepository.save(booking);

            customer.setWallet(customer.getWallet() - deposit);
            customerRepository.save(customer);

            CarOwner carOwner = car.getCarOwner();
            carOwner.setWallet(carOwner.getWallet() + deposit);
            carOwnerRepository.save(carOwner);

            Transaction customerTransaction = new Transaction();
            customerTransaction.setAmount(deposit);
            customerTransaction.setType("Pay deposit");
            customerTransaction.setCustomer(customer);
            customerTransaction.setCarName(car.getName());
            customerTransaction.setBookingNo(booking.getBookingNo());
            customerTransaction.setTransactionDateTime(new Date());
            customerTransaction.setWalletBalance(customer.getWallet());
            transactionRepository.save(customerTransaction);

            Transaction ownerTransaction = new Transaction();
            ownerTransaction.setAmount(deposit);
            ownerTransaction.setType("Receive deposit");
            ownerTransaction.setCarOwner(carOwner);
            ownerTransaction.setCarName(car.getName());
            ownerTransaction.setBookingNo(booking.getBookingNo());
            ownerTransaction.setTransactionDateTime(new Date());
            ownerTransaction.setWalletBalance(carOwner.getWallet());
            transactionRepository.save(ownerTransaction);


            return "redirect:/booking/step3/" + carBookings.get(0).getBooking().getId();
        }

        model.addAttribute("error", "Unable to process booking.");
        return "booking/step2";
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
        int randomNum = (int) (Math.random() * 100000);
        return String.format("BK%05d", randomNum);
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