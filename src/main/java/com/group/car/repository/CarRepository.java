package com.group.car.repository;

import com.group.car.models.Car;
import com.group.car.models.CarOwner;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface CarRepository extends JpaRepository<Car,Long> {
    List<Car> findByCarOwner(CarOwner carOwner);
    Object findById(long id, Sort id1);
    List<Car> findByCarOwnerId(long carOwnerId);

    @Query("select c from Car c where c.name like %?1%")
    List<Car> search(String keyword);

    @Query("SELECT c FROM Car c " +
            "JOIN c.carBookings cb " +
            "JOIN cb.booking b " +
            "WHERE c.address = :adress and b.startDateTime not between :star and :end and b.endDateTime not between :star and :end")
    List<Car> searchCarDate(String adress, Date star, Date end);


    List<Car> findByAddressContainingIgnoreCase(String address);

    @Query("SELECT c FROM Car c WHERE c.address LIKE %:location% AND c.id NOT IN " +
            "(SELECT cb.car.id FROM CarBooking cb WHERE " +
            "(:startDateTime BETWEEN cb.booking.startDateTime AND cb.booking.endDateTime) OR " +
            "(:endDateTime BETWEEN cb.booking.startDateTime AND cb.booking.endDateTime) OR " +
            "(cb.booking.startDateTime BETWEEN :startDateTime AND :endDateTime))")
    List<Car> findAvailableCars(@Param("location") String location,
                                @Param("startDateTime") Date startDateTime,
                                @Param("endDateTime") Date endDateTime);
}
