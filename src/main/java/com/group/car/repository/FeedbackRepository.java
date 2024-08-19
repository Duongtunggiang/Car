package com.group.car.repository;

import com.group.car.models.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    @Query("SELECT AVG(f.ratings) FROM Feedback f JOIN f.booking b JOIN b.carBookings cb WHERE cb.car.id = :carId")
    Double getAverageRatingForCar(@Param("carId") Long carId);

}