package com.group.car.repository;

import com.group.car.models.CarBooking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CarBookingRepository extends JpaRepository<CarBooking, Long> {
//    List<CarBooking> findOverlappingBookings(Car car, Date startDate, Date endDate);
    List<CarBooking> findByCarId(Long carId);
    List<CarBooking> findByBookingId(Long id);
}