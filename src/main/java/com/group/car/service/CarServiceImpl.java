package com.group.car.service;

import com.group.car.models.Booking;
import com.group.car.models.Car;
import com.group.car.models.CarBooking;
import com.group.car.repository.CarBookingRepository;
import com.group.car.repository.CarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CarServiceImpl implements CarService {

    @Autowired
    private CarRepository carRepository;
    public Car findById(Long id) {
        Optional<Car> car = carRepository.findById(id);
        return car.orElse(null);
    }

    @Autowired
    private CarBookingRepository carBookingRepository;

//    public Car findById(Long id) {
//        Optional<Car> car = carRepository.findById(id);
//        return car.orElse(null);
//    }

    public Car saveCar(Car car) {
        return carRepository.save(car);
    }

    public void deleteById(Long id) {
        carRepository.deleteById(id);
    }

    public Iterable<Car> findAll() {
        return carRepository.findAll();
    }

    public boolean existsById(Long id) {
        return carRepository.existsById(id);
    }

    public void addCarToBooking(Car car, Booking booking) {
        CarBooking carBooking = new CarBooking();
        carBooking.setCar(car);
        carBooking.setBooking(booking);
        carBookingRepository.save(carBooking);
    }

    public void removeCarFromBooking(Car car, Booking booking) {
        carBookingRepository.findAll().forEach(carBooking -> {
            if (carBooking.getCar().equals(car) && carBooking.getBooking().equals(booking)) {
                carBookingRepository.delete(carBooking);
            }
        });
    }

    @Override
    public List<Car> findAllByCarOwner(long carOwnerId) {
        return List.of();
    }
}