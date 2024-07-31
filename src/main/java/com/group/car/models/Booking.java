package com.group.car.models;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "Booking")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String bookingNo;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private String driversInformation;
    private String paymentMethod;
    private String status;

    private String pickUpLocation;



    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CarBooking> carBookings;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @OneToOne(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    private Feedback feedback;

    public String getPickUpLocation() {
        return pickUpLocation;
    }

    public void setPickUpLocation(String pickUpLocation) {
        this.pickUpLocation = pickUpLocation;
    }

    public long getId() {
        return id;
    }

    public String getBookingNo() {
        return bookingNo;
    }

    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }

    public String getDriversInformation() {
        return driversInformation;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public String getStatus() {
        return status;
    }

    public List<CarBooking> getCarBookings() {
        return carBookings;
    }

    public Customer getCustomer() {
        return customer;
    }

    public Feedback getFeedback() {
        return feedback;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setBookingNo(String bookingNo) {
        this.bookingNo = bookingNo;
    }

    public void setStartDateTime(LocalDateTime startDateTime) {
        this.startDateTime = startDateTime;
    }

    public void setEndDateTime(LocalDateTime endDateTime) {
        this.endDateTime = endDateTime;
    }

    public void setDriversInformation(String driversInformation) {
        this.driversInformation = driversInformation;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setCarBookings(List<CarBooking> carBookings) {
        this.carBookings = carBookings;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public void setFeedback(Feedback feedback) {
        this.feedback = feedback;
    }
}
