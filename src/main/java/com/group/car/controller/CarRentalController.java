package com.group.car.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller()
@RequestMapping("cars")
public class CarRentalController {

    @GetMapping("/rent-a-car")
    public String showRentalCarForm(Model model) {
//        model.addAttribute("pickupLocation", "Thang Kiet, Cau Giay, Hanoi");
//        model.addAttribute("pickupDateTime", "13/03/2022 - 12:00 PM");
//        model.addAttribute("returnDateTime", "22/03/2022 - 14:00 PM");
//        model.addAttribute("carModel", "Nissan Navara El 2017");
//        model.addAttribute("pricePerDay", "900,000");
//        model.addAttribute("carLocation", "Cau Giay, Hanoi");
//        model.addAttribute("numberOfDays", 15);
//        model.addAttribute("totalPrice", "13,500,000");
//        model.addAttribute("deposit", "15,000,000");

        return "car-rental-form";
    }


    @GetMapping("/car-details")
    public String showCarDetails(Model model) {
//        model.addAttribute("pickupLocation", "Thang Kiet, Cau Giay, Hanoi");
//        model.addAttribute("pickupDateTime", "13/03/2022 - 12:00 PM");
//        model.addAttribute("returnDateTime", "22/03/2022 - 14:00 PM");
//        model.addAttribute("carModel", "Nissan Navara El 2017");
//        model.addAttribute("pricePerDay", "900,000");
//        model.addAttribute("carLocation", "Cau Giay, Hanoi");
//        model.addAttribute("numberOfDays", 15);
//        model.addAttribute("totalPrice", "13,500,000");
//        model.addAttribute("deposit", "15,000,000");

        return "car-details";
    }
}