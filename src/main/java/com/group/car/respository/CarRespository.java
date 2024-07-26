package com.group.car.respository;

import com.group.car.models.Car;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarRespository extends JpaRepository<Car,Long> {
}
