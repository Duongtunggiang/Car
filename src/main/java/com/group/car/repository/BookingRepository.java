package com.group.car.repository;

import com.group.car.models.Booking;
import com.group.car.models.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Object findByCustomer(Customer customer);
}