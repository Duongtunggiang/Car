package com.group.car.repository;

import com.group.car.models.CarOwner;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarOwnerRepository extends JpaRepository<CarOwner, Long> {
}