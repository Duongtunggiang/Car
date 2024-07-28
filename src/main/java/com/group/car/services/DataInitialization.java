package com.group.car.services;

import com.group.car.models.*;
import com.group.car.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class DataInitialization implements CommandLineRunner {

    @Autowired
    private CarOwnerRepository carOwnerRepository;
    @Autowired
    private CarRepository carRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private FeedbackRepository feedbackRepository;

    @Override
    public void run(String... args) throws Exception {
        // CarOwner sample data
        CarOwner owner1 = new CarOwner();
        owner1.setName("Nguyen Van A");
        owner1.setDateOfBirth(LocalDate.of(1980, 1, 15));
        owner1.setNationalIdNo("123456789");
        owner1.setPhoneNo("0901234567");
        owner1.setEmail("nguyenvana@email.com");
        owner1.setAddress("Hanoi, Vietnam");
        owner1.setDrivingLicense("DL123456");
        owner1.setWallet(5000000.0);
        carOwnerRepository.save(owner1);

        CarOwner owner2 = new CarOwner();
        owner2.setName("Tran Thi B");
        owner2.setDateOfBirth(LocalDate.of(1975, 5, 20));
        owner2.setNationalIdNo("234567890");
        owner2.setPhoneNo("0912345678");
        owner2.setEmail("tranthib@email.com");
        owner2.setAddress("Ho Chi Minh City, Vietnam");
        owner2.setDrivingLicense("DL234567");
        owner2.setWallet(4000000.0);
        carOwnerRepository.save(owner2);

        CarOwner owner3 = new CarOwner();
        owner3.setName("Pham Van C");
        owner3.setDateOfBirth(LocalDate.of(1985, 2, 10));
        owner3.setNationalIdNo("345678901");
        owner3.setPhoneNo("0923456789");
        owner3.setEmail("phamvanc@email.com");
        owner3.setAddress("Da Nang, Vietnam");
        owner3.setDrivingLicense("DL345678");
        owner3.setWallet(6000000.0);
        carOwnerRepository.save(owner3);

        // Car sample data
        Car car1 = new Car();
        car1.setName("Nissan Navara");
        car1.setLicensePlate("30A-12345");
        car1.setBrand("Nissan");
        car1.setModel("Navara");
        car1.setColor("Black");
        car1.setNumberOfSeats(5);
        car1.setProductionYear(2020);
        car1.setTransmissionType("Automatic");
        car1.setFuelType("Diesel");
        car1.setMileage(50000);
        car1.setFuelConsumption(7.5f);
        car1.setBasePrice(1000000.0);
        car1.setDeposit(5000000.0);
        car1.setAddress("Hanoi, Vietnam");
        car1.setDescription("Good condition pickup truck");
        car1.setOwner(owner1);
        carRepository.save(car1);

        Car car2 = new Car();
        car2.setName("Toyota Camry");
        car2.setLicensePlate("31B-67890");
        car2.setBrand("Toyota");
        car2.setModel("Camry");
        car2.setColor("White");
        car2.setNumberOfSeats(4);
        car2.setProductionYear(2018);
        car2.setTransmissionType("Automatic");
        car2.setFuelType("Petrol");
        car2.setMileage(40000);
        car2.setFuelConsumption(6.5f);
        car2.setBasePrice(800000.0);
        car2.setDeposit(4000000.0);
        car2.setAddress("Ho Chi Minh City, Vietnam");
        car2.setDescription("Comfortable and fuel-efficient sedan");
        car2.setOwner(owner2);
        carRepository.save(car2);

        Car car3 = new Car();
        car3.setName("Ford Ranger");
        car3.setLicensePlate("32C-34567");
        car3.setBrand("Ford");
        car3.setModel("Ranger");
        car3.setColor("Blue");
        car3.setNumberOfSeats(5);
        car3.setProductionYear(2021);
        car3.setTransmissionType("Manual");
        car3.setFuelType("Diesel");
        car3.setMileage(30000);
        car3.setFuelConsumption(8.0f);
        car3.setBasePrice(1200000.0);
        car3.setDeposit(6000000.0);
        car3.setAddress("Da Nang, Vietnam");
        car3.setDescription("Powerful and reliable pickup truck");
        car3.setOwner(owner3);
        carRepository.save(car3);

        // Customer sample data
        Customer customer1 = new Customer();
        customer1.setName("Le Van C");
        customer1.setDateOfBirth(LocalDate.of(1990, 3, 10));
        customer1.setNationalIdNo("456789123");
        customer1.setPhoneNo("0905555555");
        customer1.setEmail("levanc@email.com");
        customer1.setAddress("Da Nang, Vietnam");
        customer1.setDrivingLicense("DL789456");
        customer1.setWallet(2000000.0);
        customerRepository.save(customer1);

        Customer customer2 = new Customer();
        customer2.setName("Hoang Thi D");
        customer2.setDateOfBirth(LocalDate.of(1988, 8, 25));
        customer2.setNationalIdNo("567890234");
        customer2.setPhoneNo("0916666666");
        customer2.setEmail("hoangthid@email.com");
        customer2.setAddress("Hanoi, Vietnam");
        customer2.setDrivingLicense("DL890567");
        customer2.setWallet(3000000.0);
        customerRepository.save(customer2);

        Customer customer3 = new Customer();
        customer3.setName("Nguyen Van E");
        customer3.setDateOfBirth(LocalDate.of(1995, 7, 15));
        customer3.setNationalIdNo("678901345");
        customer3.setPhoneNo("0927777777");
        customer3.setEmail("nguyenvane@email.com");
        customer3.setAddress("Ho Chi Minh City, Vietnam");
        customer3.setDrivingLicense("DL901678");
        customer3.setWallet(2500000.0);
        customerRepository.save(customer3);

        // Booking sample data
        Booking booking1 = new Booking();
        booking1.setBookingNo("BK001");
        booking1.setCar(car1);
        booking1.setCustomer(customer1);
        booking1.setStartDateTime(LocalDateTime.of(2023, 6, 1, 10, 0));
        booking1.setEndDateTime(LocalDateTime.of(2023, 6, 5, 10, 0));
        booking1.setDriverInformation("Self-drive");
        booking1.setPaymentMethod("Credit Card");
        booking1.setStatus("Completed");
        bookingRepository.save(booking1);

        Booking booking2 = new Booking();
        booking2.setBookingNo("BK002");
        booking2.setCar(car2);
        booking2.setCustomer(customer2);
        booking2.setStartDateTime(LocalDateTime.of(2023, 7, 10, 9, 0));
        booking2.setEndDateTime(LocalDateTime.of(2023, 7, 15, 9, 0));
        booking2.setDriverInformation("Self-drive");
        booking2.setPaymentMethod("Debit Card");
        booking2.setStatus("Completed");
        bookingRepository.save(booking2);

        Booking booking3 = new Booking();
        booking3.setBookingNo("BK003");
        booking3.setCar(car3);
        booking3.setCustomer(customer3);
        booking3.setStartDateTime(LocalDateTime.of(2023, 8, 5, 8, 0));
        booking3.setEndDateTime(LocalDateTime.of(2023, 8, 10, 8, 0));
        booking3.setDriverInformation("Self-drive");
        booking3.setPaymentMethod("Cash");
        booking3.setStatus("Completed");
        bookingRepository.save(booking3);

        // Feedback sample data
        Feedback feedback1 = new Feedback();
        feedback1.setBooking(booking1);
        feedback1.setRatings(5);
        feedback1.setContent("Great car and service!");
        feedback1.setDateTime(LocalDateTime.of(2023, 6, 6, 9, 30));
        feedbackRepository.save(feedback1);

        Feedback feedback2 = new Feedback();
        feedback2.setBooking(booking2);
        feedback2.setRatings(4);
        feedback2.setContent("Good car, but a bit expensive.");
        feedback2.setDateTime(LocalDateTime.of(2023, 7, 16, 10, 0));
        feedbackRepository.save(feedback2);

        Feedback feedback3 = new Feedback();
        feedback3.setBooking(booking3);
        feedback3.setRatings(5);
        feedback3.setContent("Excellent experience. Highly recommend!");
        feedback3.setDateTime(LocalDateTime.of(2023, 8, 11, 11, 0));
        feedbackRepository.save(feedback3);
    }
}
