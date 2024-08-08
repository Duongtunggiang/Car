package com.group.car.repository;

import com.group.car.models.Car;
import com.group.car.models.CarOwner;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface CarRepository extends JpaRepository<Car,Long> {
//    List<Car> findByCarOwner(CarOwner carOwner);
//    Object findById(long id, Sort id1);
    List<Car> findByCarOwnerId(long carOwnerId);
//    Car findById(long id);
}
