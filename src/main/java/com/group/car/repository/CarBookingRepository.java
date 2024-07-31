package com.group.car.repository;

import com.group.car.models.CarBooking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarBookingRepository extends JpaRepository<CarBooking, Long> {
}