package com.group.car.controller;

import com.group.car.models.Car;
import com.group.car.repository.CarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Arrays;
import java.util.Optional;

@Controller()
@RequestMapping("customer")
public class CarController {
    @Autowired
    private CarRepository carRepository;

    @GetMapping("/rent-a-car")
    public String showRentalCarForm(Model model) {
        return "car-rental-form";
    }




    @GetMapping("/car-details")
    public String showCarDetails(Model model) {
        Car car1 = new Car();
        car1.setName("Nissan Navara");
        car1.setLicensePlate("30A-12347");
        car1.setBrand("Nissan");
        car1.setModel("Navara");
        car1.setColor("Black");
        car1.setNumberOfSeats(5);
        car1.setProductionYear(2020);
        car1.setTransmissionType("Automatic");
        car1.setFuelType("Diesel");
        car1.setMileage(50000);
        car1.setFuelConsumption(7.5f);
        car1.setBasePrice(1000000.0);
        car1.setDeposit(5000000.0);
        car1.setAddress("Hanoi, Vietnam");
        car1.setDescription("Good condition pickup truck");
        car1.setImages(Arrays.asList(
                "https://imgd.aeplcdn.com/80x52/n/cw/ec/139651/curvv-ice-exterior-grille.jpeg?isig=0&q=80",
                "https://imgd.aeplcdn.com/370x208/n/cw/ec/132427/taisor-exterior-right-front-three-quarter-2.png?isig=0&q=80"
        ));

        model.addAttribute("car", car1);

        return "car-details";

//        Optional<Car> carOptional = carRepository.findById(1L);
//        if (carOptional.isPresent()) {
//            Car car = carOptional.get();
//            model.addAttribute("car", car);
//        }
//        return "car-details";
    }
}