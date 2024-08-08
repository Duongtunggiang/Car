package com.group.car.controller;

import com.group.car.models.Dto.TotalCalculationRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.text.DecimalFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
public class SumMoneyControll {

    @PostMapping("/calculate-total")
    public ResponseEntity<Map<String, String>> calculateTotal(@RequestBody TotalCalculationRequest request) {
        try {
            int numberOfDays = request.getNumberOfDays();
            int pricePerDay = request.getPricePerDay();
            int totalPrice = numberOfDays * pricePerDay;

            DecimalFormat formatter = new DecimalFormat("#,###");
            String formattedTotalPrice = formatter.format(totalPrice) + " VND";

            Map<String, String> response = new HashMap<>();
            response.put("totalPrice", formattedTotalPrice);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("error", "Error calculating total price!!!."));
        }
    }
}
