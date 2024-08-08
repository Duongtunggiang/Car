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
    private Date startDateTime;
    private Date endDateTime;
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

    public void addCarBooking(CarBooking carBooking) {
        this.carBookings.add(carBooking);
        carBooking.setBooking(this);
    }

    public void removeCarBooking(CarBooking carBooking) {
        this.carBookings.remove(carBooking);
        carBooking.setBooking(null);
    }

    //Thêm cái này vào để lưu thông tin khách hàng thuê xe
//    private String renterName;
//    private LocalDate renterDateOfBirth;
//    private String renterPhoneNo;
//    private String renterEmail;
//    private String renterAddress;
//    private String renterDrivingLicense;
    //------

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

    public Date getStartDateTime() {
        return startDateTime;
    }

    public Date getEndDateTime() {
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

    public void setStartDateTime(Date startDateTime) {
        this.startDateTime = startDateTime;
    }

    public void setEndDateTime(Date endDateTime) {
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
