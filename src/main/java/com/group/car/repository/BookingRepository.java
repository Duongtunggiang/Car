package com.group.car.repository;

import com.group.car.models.Booking;
import com.group.car.models.CarBooking;
import com.group.car.models.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Object findByCustomer(Customer customer);
    List<Booking> findByStatus(String status);
    Optional<Booking> findById(Long bookingId);
}