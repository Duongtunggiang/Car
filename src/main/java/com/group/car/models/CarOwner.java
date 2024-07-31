package com.group.car.models;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "CarOwner")
public class CarOwner {
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



    @OneToMany(mappedBy = "carOwner", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Car> cars;

    @OneToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    // Getters and setters



    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public String getNationalIdNo() {
        return nationalIdNo;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public String getEmail() {
        return email;
    }

    public String getAddress() {
        return address;
    }

    public String getDrivingLicense() {
        return drivingLicense;
    }

    public int getWallet() {
        return wallet;
    }

    public List<Car> getCars() {
        return cars;
    }

    public Account getAccount() {
        return account;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public void setNationalIdNo(String nationalIdNo) {
        this.nationalIdNo = nationalIdNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setDrivingLicense(String drivingLicense) {
        this.drivingLicense = drivingLicense;
    }

    public void setWallet(int wallet) {
        this.wallet = wallet;
    }

    public void setCars(List<Car> cars) {
        this.cars = cars;
    }

    public void setAccount(Account account) {
        this.account = account;
    }
}
