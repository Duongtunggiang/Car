package com.group.car.controller;

import com.group.car.models.*;
import com.group.car.models.Dto.BookingDto;
import com.group.car.models.Dto.BookingFormDTO;
import com.group.car.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/booking")
public class BookingController {
    @Autowired
    private CarOwnerRepository carOwnerRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private CarRepository carRepository;

    //Rent a car -- Duong

    @GetMapping("/rent-a-car")
    public String showRentalCarForm(@RequestParam("id") long id, Model model) {
        Car car = carRepository.findById(id).orElse(null);
        if (car == null) {
            model.addAttribute("message", "Car not found");
            return "car-not-found";
        }
        Booking booking = new Booking();

        model.addAttribute("car", car);
        model.addAttribute("booking", booking);
        return "customer/car-rental-form";
    }


    @PostMapping("/rent")
    public String rentCar(@ModelAttribute Booking booking, @RequestParam("carIds") List<Long> carIds, Model model) {
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

    @GetMapping("/booking-details")
    public String viewBookingDetails(Model model) {
        Booking booking = bookingRepository.findById(1L).orElse(null);
        if (booking != null) {
            model.addAttribute("booking", booking);
        } else {
            model.addAttribute("error", "Booking not found.");
        }

        return "customer/car-rental-form";
    }

    @PostMapping("/booking-detail")
    public String submitBooking(@RequestParam("startDateTime") Date startDateTime,
                                @RequestParam("endDateTime") Date endDateTime,
                                @RequestParam("pickUpLocation") String pickUpLocation,
                                Model model) {
        try {
            if (endDateTime.before(startDateTime)) {
                model.addAttribute("error", "End date must be after start date.");
                return "customer/car-rental-form";
            }

            Booking booking = new Booking();
            booking.setStartDateTime(startDateTime);
            booking.setEndDateTime(endDateTime);
            booking.setPickUpLocation(pickUpLocation);

            bookingRepository.save(booking);

            model.addAttribute("message", "Booking saved successfully!");
            return "redirect:/booking/confirmation";
        } catch (Exception e) {
            model.addAttribute("error", "An error occurred while saving the booking. Please try again.");
            return "customer/car-rental-form";
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