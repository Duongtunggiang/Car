package com.group.car.controller;

import com.group.car.models.*;
import com.group.car.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
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
    public String showRentalCarForm(@RequestParam("id") long id,Model model) {
        Car car = carRepository.findById(id).orElse(null);
        if (car == null) {
            model.addAttribute("message", "Car not found");
            return "car-not-found";
        }
        Booking booking = new Booking();
//        booking.setPickUpLocation(car.getAddress());

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
}