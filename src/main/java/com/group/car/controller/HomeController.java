package com.group.car.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @GetMapping({"","/"})
    public String home(){
        return "index";
    }

    @GetMapping("/home")
    public String home(Model model) {
        model.addAttribute("customerName", "An");

        return "home-page-as-customer";
    }
}
