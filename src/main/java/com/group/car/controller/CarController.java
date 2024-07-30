package com.group.car.controller;

import com.group.car.models.Car;
import com.group.car.repository.CarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Controller()
@RequestMapping("/customer")
public class CarController {
    @Autowired
    private CarRepository carRepository;

    @GetMapping("/")
    public String homeCustomer(Model model){
        model.addAttribute("");
        List<Car> cars = carRepository.findAll();
        model.addAttribute("cars", cars);
        return "home";
    }

    @GetMapping("/rent-a-car")
    public String showRentalCarForm(Model model) {
        return "customer/car-rental-form";
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
}