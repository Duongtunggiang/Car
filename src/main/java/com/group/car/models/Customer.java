package com.group.car.models;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "Customer")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;
    private LocalDate dateOfBirth;
    private String nationalIdNo;
    private String phoneNo;

    @Column(unique = true, nullable = false)
    private String email;

    private String address;
    private String drivingLicense;
    private int wallet;

    //Thêm cái này vào để lưu thông tin khách hàng thuê xe
    private String renterName;
    private LocalDate renterDateOfBirth;
    private String renterPhoneNo;
    private String renterEmail;
    private String renterAddress;
    private String renterDrivingLicense;
    //------

//    private String coverImage;
//
//    private String image;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Booking> bookings;

    @OneToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    // Getters and setters
    public String getRenterName() {
        return renterName;
    }

    public LocalDate getRenterDateOfBirth() {
        return renterDateOfBirth;
    }

    public String getRenterPhoneNo() {
        return renterPhoneNo;
    }

    public String getRenterEmail() {
        return renterEmail;
    }

    public String getRenterAddress() {
        return renterAddress;
    }

    public String getRenterDrivingLicense() {
        return renterDrivingLicense;
    }

    public void setRenterName(String renterName) {
        this.renterName = renterName;
    }

    public void setRenterDateOfBirth(LocalDate renterDateOfBirth) {
        this.renterDateOfBirth = renterDateOfBirth;
    }

    public void setRenterPhoneNo(String renterPhoneNo) {
        this.renterPhoneNo = renterPhoneNo;
    }

    public void setRenterEmail(String renterEmail) {
        this.renterEmail = renterEmail;
    }

    public void setRenterAddress(String renterAddress) {
        this.renterAddress = renterAddress;
    }

    public void setRenterDrivingLicense(String renterDrivingLicense) {
        this.renterDrivingLicense = renterDrivingLicense;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate  getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getNationalIdNo() {
        return nationalIdNo;
    }

    public void setNationalIdNo(String nationalIdNo) {
        this.nationalIdNo = nationalIdNo;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDrivingLicense() {
        return drivingLicense;
    }

    public void setDrivingLicense(String drivingLicense) {
        this.drivingLicense = drivingLicense;
    }

    public int getWallet() {
        return wallet;
    }

    public void setWallet(int wallet) {
        this.wallet = wallet;
    }

    public List<Booking> getBookings() {
        return bookings;
    }

    public void setBookings(List<Booking> bookings) {
        this.bookings = bookings;
    }
}
