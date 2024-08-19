package com.group.car.repository;

import com.group.car.models.CarBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface CarBookingRepository extends JpaRepository<CarBooking, Long> {
//    List<CarBooking> findOverlappingBookings(Car car, Date startDate, Date endDate);
    List<CarBooking> findByCarId(Long carId);
    List<CarBooking> findByBookingId(Long id);
    long countByCarId(Long carId);

    @Query("SELECT NEW map(b.startDateTime as startDate, b.endDateTime as endDate) " +
            "FROM CarBooking cb JOIN cb.booking b WHERE cb.car.id = :carId")
    List<Map<String, Date>> findBookingDatesByCar(@Param("carId") Long carId);
}