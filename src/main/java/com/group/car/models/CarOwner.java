package com.group.car.models;

import jakarta.persistence.*;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "CarOwner")
public class CarOwner {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String Name;

    private Date DateOfBirth;

    private String NationalIdNo;
    private String PhoneNo;

    @Column(unique = true, nullable = false)
    private String Email;

    private String Address;
    private String Drivinglicense;
    private String Wallet;

    @OneToMany(mappedBy = "carOwner", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Car> cars;

    @OneToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    public void setAccount(Account account) {
        this.account = account;
    }

    public Account getAccount() {
        return account;
    }

    public void setCars(List<Car> cars) {
        this.cars = cars;
    }

    public List<Car> getCars() {
        return cars;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        Name = name;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        DateOfBirth = dateOfBirth;
    }

    public void setNationalIdNo(String nationalIdNo) {
        NationalIdNo = nationalIdNo;
    }

    public void setPhoneNo(String phoneNo) {
        PhoneNo = phoneNo;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public void setDrivinglicense(String drivinglicense) {
        Drivinglicense = drivinglicense;
    }

    public void setWallet(String wallet) {
        Wallet = wallet;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return Name;
    }

    public Date getDateOfBirth() {
        return DateOfBirth;
    }

    public String getNationalIdNo() {
        return NationalIdNo;
    }

    public String getPhoneNo() {
        return PhoneNo;
    }

    public String getEmail() {
        return Email;
    }

    public String getAddress() {
        return Address;
    }

    public String getDrivinglicense() {
        return Drivinglicense;
    }

    public String getWallet() {
        return Wallet;
    }
}
