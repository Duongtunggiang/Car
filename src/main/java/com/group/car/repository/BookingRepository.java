package com.group.car.repository;

import com.group.car.models.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, Long> {
//    Booking findByCarId(Long carId);
}