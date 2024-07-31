package com.group.car.service;

import com.group.car.models.Car;
import com.group.car.repository.CarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CarServiceImpl implements CarService {

    @Autowired
    private CarRepository carRepository;

    @Override
    public List<Car> findAllByCarOwner(long carOwnerId) {
        return carRepository.findByCarOwnerId(carOwnerId);
    }
}