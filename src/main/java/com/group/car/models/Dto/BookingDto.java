package com.group.car.models.Dto;

import java.time.LocalDateTime;

public class BookingDto {
    private String bookingNo;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private String driversInformation;
    private String paymentMethod;
    private String status;

    private String pickUpLocation;

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

    public String getPickUpLocation() {
        return pickUpLocation;
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

    public void setPickUpLocation(String pickUpLocation) {
        this.pickUpLocation = pickUpLocation;
    }
}
