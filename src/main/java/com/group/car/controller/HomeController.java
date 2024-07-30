package com.group.car.controller;

import com.group.car.models.Car;
import com.group.car.repository.CarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private CarRepository carRepository;

    @GetMapping({"","/"})
    public String home(
    Model model){
        model.addAttribute("");
        List<Car> cars = carRepository.findAll();
        model.addAttribute("cars", cars);
        return "home";
    }
}
