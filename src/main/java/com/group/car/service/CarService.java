package com.group.car.service;

import com.group.car.models.Car;
import org.springframework.stereotype.Service;

import java.util.List;

public interface CarService {
    List<Car> findAllByCarOwner(long carOwnerId);
}