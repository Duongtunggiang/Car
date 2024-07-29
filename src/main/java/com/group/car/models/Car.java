package com.group.car.models;

import jakarta.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "Car")
public class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;
    private String licensePlate;
    private String brand;
    private String model;
    private String color;
    private int numbersOfSeats;
    private Date productionYears;
    private String transmissionType;
    private String fuelType;
    private String mileage;
    private String fuelConsumption;
    private double basicPrice;
    private double deposit;
    private String address;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String additionalFunctions;
    private String termsOfUse;
    private String images;

    @ManyToOne
    @JoinColumn(name = "carOwner_id", nullable = false)
    private CarOwner carOwner;

    @OneToMany(mappedBy = "car", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CarBooking> carBookings;

    public Date getProductionYears() {
        return productionYears;
    }

    public void setProductionYears(Date productionYears) {
        this.productionYears = productionYears;
    }

    public String getName() {
        return name;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public String getBrand() {
        return brand;
    }

    public String getModel() {
        return model;
    }

    public String getColor() {
        return color;
    }

    public int getNumbersOfSeats() {
        return numbersOfSeats;
    }


    public String getTransmissionType() {
        return transmissionType;
    }

    public String getFuelType() {
        return fuelType;
    }

    public String getMileage() {
        return mileage;
    }

    public String getFuelConsumption() {
        return fuelConsumption;
    }

    public double getBasicPrice() {
        return basicPrice;
    }

    public double getDeposit() {
        return deposit;
    }

    public String getAddress() {
        return address;
    }

    public String getDescription() {
        return description;
    }

    public String getAdditionalFunctions() {
        return additionalFunctions;
    }

    public String getTermsOfUse() {
        return termsOfUse;
    }

    public String getImages() {
        return images;
    }

    public CarOwner getCarOwner() {
        return carOwner;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setNumbersOfSeats(int numbersOfSeats) {
        this.numbersOfSeats = numbersOfSeats;
    }

    public void setTransmissionType(String transmissionType) {
        this.transmissionType = transmissionType;
    }

    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }

    public void setMileage(String mileage) {
        this.mileage = mileage;
    }

    public void setFuelConsumption(String fuelConsumption) {
        this.fuelConsumption = fuelConsumption;
    }

    public void setBasicPrice(double basicPrice) {
        this.basicPrice = basicPrice;
    }

    public void setDeposit(double deposit) {
        this.deposit = deposit;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAdditionalFunctions(String additionalFunctions) {
        this.additionalFunctions = additionalFunctions;
    }

    public void setTermsOfUse(String termsOfUse) {
        this.termsOfUse = termsOfUse;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public void setCarOwner(CarOwner carOwner) {
        this.carOwner = carOwner;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


    public List<CarBooking> getCarBookings() {
        return carBookings;
    }

    public void setCarBookings(List<CarBooking> carBookings) {
        this.carBookings = carBookings;
    }
}
