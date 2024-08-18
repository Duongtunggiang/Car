package com.group.car.models;

import jakarta.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Entity
@Table(name = "Booking")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String bookingNo;
//    private Date startDateTime;
//    private Date endDateTime;

    @DateTimeFormat(pattern = "dd-MM-dd'T'HH:mm")
    private Date startDateTime;

    @DateTimeFormat(pattern = "dd-MM-dd'T'HH:mm")
    private Date endDateTime;
    private String driversInformation;
    private String paymentMethod;
    private String status;
    private String pickUpLocation;

    @Transient
    private int numberOfDays;

    @Transient
    private int totalPrice;

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CarBooking> carBookings = new ArrayList<>(); // Initialize the list

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @OneToOne(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    private Feedback feedback;


    // Getters and Setters
    public void setNumberOfDays(int numberOfDays) {
        this.numberOfDays = numberOfDays;
    }

    public void setTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getBookingNo() {
        return bookingNo;
    }

    public void setBookingNo(String bookingNo) {
        this.bookingNo = bookingNo;
    }

    public Date getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(Date startDateTime) {
        this.startDateTime = startDateTime;
        calculateBookingDetails();
    }

    public Date getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(Date endDateTime) {
        this.endDateTime = endDateTime;
        calculateBookingDetails();
    }

    public String getDriversInformation() {
        return driversInformation;
    }

    public void setDriversInformation(String driversInformation) {
        this.driversInformation = driversInformation;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPickUpLocation() {
        return pickUpLocation;
    }

    public void setPickUpLocation(String pickUpLocation) {
        this.pickUpLocation = pickUpLocation;
    }

    public List<CarBooking> getCarBookings() {
        return carBookings;
    }

    public void setCarBookings(List<CarBooking> carBookings) {
        this.carBookings = carBookings;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Feedback getFeedback() {
        return feedback;
    }

    public void setFeedback(Feedback feedback) {
        this.feedback = feedback;
    }

    public int getNumberOfDays() {
        return numberOfDays;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    // Helper method to calculate number of days and total price
    private void calculateBookingDetails() {
        if (startDateTime != null && endDateTime != null) {
            long diffInMillies = Math.abs(endDateTime.getTime() - startDateTime.getTime());
            this.numberOfDays = (int) TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);

            if (!carBookings.isEmpty()) {
                Car car = carBookings.get(0).getCar();
                this.totalPrice = car.getBasicPrice() * (this.numberOfDays > 0 ? this.numberOfDays : 1);
            }
        }
    }
}
