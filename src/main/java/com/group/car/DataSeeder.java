package com.group.car;

import com.group.car.models.*;
import com.group.car.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Calendar;
import java.util.Date;
import java.util.Set;

@Configuration
public class DataSeeder {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner initDatabase(
            RoleRepository roleRepository,
            AccountRepository accountRepository,
            CustomerRepository customerRepository,
            CarOwnerRepository carOwnerRepository,
            CarRepository carRepository,
            BookingRepository bookingRepository,
            CarBookingRepository carBookingRepository,
            FeedbackRepository feedbackRepository) {
        return args -> {
            // Check if roles exist, if not, create them
            if (roleRepository.findAll().isEmpty()) {
                Role customerRole = new Role();
                customerRole.setName("Customer");
                Role carOwnerRole = new Role();
                carOwnerRole.setName("CarOwner");
                roleRepository.save(customerRole);
                roleRepository.save(carOwnerRole);
            }

            Role customerRole = roleRepository.findByName("Customer");
            Role carOwnerRole = roleRepository.findByName("CarOwner");

            // Create accounts
            Account account1 = new Account();
            account1.setUsername("customer01");
            account1.setEmail("customer01@gmail.com");
            account1.setPassword(passwordEncoder.encode("password"));
            account1.setRoles(Set.of(customerRole));
            accountRepository.save(account1);

            Account account2 = new Account();
            account2.setUsername("carowner01");
            account2.setEmail("carowner01@gmail.com");
            account2.setPassword(passwordEncoder.encode("password"));
            account2.setRoles(Set.of(carOwnerRole));
            accountRepository.save(account2);

            // Create customer
            Customer customer1 = new Customer();
            customer1.setName("Jone");
            customer1.setDateOfBirth(new Date());
            customer1.setNationalIdNo("123456789");
            customer1.setPhoneNo("0123456789");
            customer1.setEmail("customer01@gmail.com");
            customer1.setAddress("123 Main St, City");
            customer1.setDrivingLicense("DL123456");
            customer1.setWallet(1000);
            customer1.setAccount(account1);
            customerRepository.save(customer1);

            // Create car owner
            CarOwner carOwner1 = new CarOwner();
            carOwner1.setName("carowner01");
            carOwner1.setDateOfBirth(new Date());
            carOwner1.setNationalIdNo("987654321");
            carOwner1.setPhoneNo("9876543210");
            carOwner1.setEmail("carowner01@gmail.com");
            carOwner1.setAddress("456 Oak St, Town");
            carOwner1.setDrivingLicense("DL987654");
            carOwner1.setWallet(5000);
            carOwner1.setAccount(account2);
            carOwnerRepository.save(carOwner1);

            // Create car
            Car car1 = new Car();
            car1.setName("Nissan Navara EL 2017");
            car1.setLicensePlate("30A-12345");
            car1.setBrand("Nissan");
            car1.setModel("Navara");
            car1.setColor("Black");
            car1.setNumbersOfSeats(5);
            car1.setProductionYears(new Date());
            car1.setTransmissionType("Automatic");
            car1.setFuelType("Diesel");
            car1.setMileage("50000");
            car1.setFuelConsumption("7.5");
            car1.setBasicPrice(5000);
            car1.setDeposit(3000);
            car1.setAddress("Cau Giay, Hanoi");
            car1.setDescription("Good condition pickup truck");
            car1.setCarOwner(carOwner1);
            carRepository.save(car1);

            // Create bookings with correct dates and prices
            Calendar calendar = Calendar.getInstance();

            calendar.set(2024, Calendar.AUGUST, 1, 10, 0);
            Date startDate1 = calendar.getTime();
            calendar.set(2024, Calendar.AUGUST, 3, 10, 0);
            Date endDate1 = calendar.getTime();

            calendar.set(2024, Calendar.AUGUST, 5, 14, 0);
            Date startDate2 = calendar.getTime();
            calendar.set(2024, Calendar.AUGUST, 7, 14, 0);
            Date endDate2 = calendar.getTime();

            calendar.set(2024, Calendar.AUGUST, 10, 8, 0);
            Date startDate3 = calendar.getTime();
            calendar.set(2024, Calendar.AUGUST, 12, 8, 0);
            Date endDate3 = calendar.getTime();

            Booking booking1 = new Booking();
            booking1.setBookingNo("B001");
            booking1.setStartDateTime(startDate1);
            booking1.setEndDateTime(endDate1);
            booking1.setDriversInformation("John Doe, License: DL123456");
            booking1.setPaymentMethod("Credit Card");
            booking1.setStatus("Confirmed");
            booking1.setCustomer(customer1);
            bookingRepository.save(booking1);

            Booking booking2 = new Booking();
            booking2.setBookingNo("B002");
            booking2.setStartDateTime(startDate2);
            booking2.setEndDateTime(endDate2);
            booking2.setDriversInformation("Jane Smith, License: DL789456");
            booking2.setPaymentMethod("Debit Card");
            booking2.setStatus("Confirmed");
            booking2.setCustomer(customer1);
            bookingRepository.save(booking2);

            Booking booking3 = new Booking();
            booking3.setBookingNo("B003");
            booking3.setStartDateTime(startDate3);
            booking3.setEndDateTime(endDate3);
            booking3.setDriversInformation("Alice Brown, License: DL321654");
            booking3.setPaymentMethod("Cash");
            booking3.setStatus("Cancelled");
            booking3.setCustomer(customer1);
            bookingRepository.save(booking3);

            // Create car bookings
            CarBooking carBooking1 = new CarBooking();
            carBooking1.setCar(car1);
            carBooking1.setBooking(booking1);
            carBookingRepository.save(carBooking1);

            CarBooking carBooking2 = new CarBooking();
            carBooking2.setCar(car1);
            carBooking2.setBooking(booking2);
            carBookingRepository.save(carBooking2);

            CarBooking carBooking3 = new CarBooking();
            carBooking3.setCar(car1);
            carBooking3.setBooking(booking3);
            carBookingRepository.save(carBooking3);

            // Create feedback
            Feedback feedback1 = new Feedback();
            feedback1.setRatings(5);
            feedback1.setContent("Great car and service!");
            feedback1.setDateTime(new Date());
            feedback1.setBooking(booking1);
            feedbackRepository.save(feedback1);

            Feedback feedback2 = new Feedback();
            feedback2.setRatings(4);
            feedback2.setContent("Good experience, but could be improved.");
            feedback2.setDateTime(new Date());
            feedback2.setBooking(booking2);
            feedbackRepository.save(feedback2);

            Feedback feedback3 = new Feedback();
            feedback3.setRatings(2);
            feedback3.setContent("The service was not up to expectations.");
            feedback3.setDateTime(new Date());
            feedback3.setBooking(booking3);
            feedbackRepository.save(feedback3);
        };
    }
}
