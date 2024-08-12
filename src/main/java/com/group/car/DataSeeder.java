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
            customer1.setWallet(100000);
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

            // Create car 2
            Car car2 = new Car();
            car2.setName("Toyota Camry LE 2020");
            car2.setLicensePlate("51A-98765");
            car2.setBrand("Toyota");
            car2.setModel("Camry");
            car2.setColor("White");
            car2.setNumbersOfSeats(5);
            car2.setProductionYears(new Date());
            car2.setTransmissionType("Automatic");
            car2.setFuelType("Petrol");
            car2.setMileage("20000");
            car2.setFuelConsumption("6.5");
            car2.setBasicPrice(8000);
            car2.setDeposit(4000);
            car2.setAddress("Cau Giay, Hanoi");
            car2.setDescription("Luxury sedan in excellent condition");
            car2.setCarOwner(carOwner1);
            carRepository.save(car2);

// Create car 3
            Car car3 = new Car();
            car3.setName("Honda CR-V 2019");
            car3.setLicensePlate("43A-67890");
            car3.setBrand("Honda");
            car3.setModel("CR-V");
            car3.setColor("Silver");
            car3.setNumbersOfSeats(7);
            car3.setProductionYears(new Date());
            car3.setTransmissionType("Automatic");
            car3.setFuelType("Petrol");
            car3.setMileage("35000");
            car3.setFuelConsumption("8.0");
            car3.setBasicPrice(7000);
            car3.setDeposit(3500);
            car3.setAddress("Thanh Khe, Da Nang");
            car3.setDescription("Spacious SUV with advanced safety features");
            car3.setCarOwner(carOwner1);
            carRepository.save(car3);

// Create car 4
            Car car4 = new Car();
            car4.setName("Ford Ranger Wildtrak 2021");
            car4.setLicensePlate("65B-54321");
            car4.setBrand("Ford");
            car4.setModel("Ranger");
            car4.setColor("Blue");
            car4.setNumbersOfSeats(5);
            car4.setProductionYears(new Date());
            car4.setTransmissionType("Automatic");
            car4.setFuelType("Diesel");
            car4.setMileage("10000");
            car4.setFuelConsumption("7.0");
            car4.setBasicPrice(9000);
            car4.setDeposit(4500);
            car4.setAddress("Can Tho City");
            car4.setDescription("New pickup truck with powerful engine");
            car4.setCarOwner(carOwner1);
            carRepository.save(car4);

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

            calendar.set(2024, Calendar.AUGUST, 13, 8, 0);
            Date startDate4 = calendar.getTime();
            calendar.set(2024, Calendar.AUGUST, 14, 8, 0);
            Date endDate4 = calendar.getTime();

            calendar.set(2024, Calendar.AUGUST, 13, 8, 0);
            Date startDate11 = calendar.getTime();
            calendar.set(2024, Calendar.AUGUST, 14, 8, 0);
            Date endDate11 = calendar.getTime();

            calendar.set(2024, Calendar.AUGUST, 12, 9, 0);
            Date startDate22 = calendar.getTime();
            calendar.set(2024, Calendar.AUGUST, 17, 8, 0);
            Date endDate22 = calendar.getTime();

            calendar.set(2024, Calendar.AUGUST, 11, 9, 0);
            Date startDate33 = calendar.getTime();
            calendar.set(2024, Calendar.AUGUST, 12, 8, 0);
            Date endDate33 = calendar.getTime();

            Booking booking1 = new Booking();
            booking1.setBookingNo("B001");
            booking1.setStartDateTime(startDate1);
            booking1.setEndDateTime(endDate1);
            booking1.setDriversInformation("John Doe, License: DL123456");
            booking1.setPaymentMethod("Credit Card");
            booking1.setStatus("Confirmed");
            booking1.setTotalPrice(10000);
            booking1.setPickUpLocation(car1.getAddress());
            booking1.setCustomer(customer1);
            bookingRepository.save(booking1);

            Booking booking2 = new Booking();
            booking2.setBookingNo("B002");
            booking2.setStartDateTime(startDate2);
            booking2.setEndDateTime(endDate2);
            booking2.setDriversInformation("Jane Smith, License: DL789456");
            booking2.setPaymentMethod("Debit Card");
            booking2.setTotalPrice(10000);
            booking2.setStatus("Confirmed");
            booking2.setPickUpLocation(car2.getAddress());
            booking2.setCustomer(customer1);
            bookingRepository.save(booking2);

            Booking booking3 = new Booking();
            booking3.setBookingNo("B003");
            booking3.setStartDateTime(startDate3);
            booking3.setEndDateTime(endDate3);
            booking3.setDriversInformation("Alice Brown, License: DL321654");
            booking3.setTotalPrice(10000);
            booking3.setPaymentMethod("Cash");
            booking3.setPickUpLocation(car3.getAddress());
            booking3.setStatus("Cancelled");
            booking3.setCustomer(customer1);
            bookingRepository.save(booking3);

            Booking booking4 = new Booking();
            booking4.setBookingNo("B004");
            booking4.setStartDateTime(startDate4);
            booking4.setTotalPrice(10000);
            booking4.setEndDateTime(endDate4);
            booking4.setDriversInformation("Alice Brown, License: DL321654");
            booking4.setPaymentMethod("Cash");
            booking4.setPickUpLocation(car3.getAddress());
            booking4.setStatus("Cancelled");
            booking4.setCustomer(customer1);
            bookingRepository.save(booking4);

            Booking booking11 = new Booking();
            booking11.setBookingNo("B011");
            booking11.setStartDateTime(startDate11);
            booking11.setEndDateTime(endDate11);
            booking11.setDriversInformation("Alice Brown, License: DL321654");
            booking11.setTotalPrice(10000);
            booking11.setPaymentMethod("Cash");
            booking11.setPickUpLocation(car2.getAddress());
            booking11.setStatus("Cancelled");
            booking11.setCustomer(customer1);
            bookingRepository.save(booking11);

            Booking booking22 = new Booking();
            booking22.setBookingNo("B022");
            booking22.setStartDateTime(startDate22);
            booking22.setTotalPrice(10000);
            booking22.setEndDateTime(endDate22);
            booking22.setDriversInformation("Alice Brown, License: DL321654");
            booking22.setPaymentMethod("Cash");
            booking22.setPickUpLocation(car3.getAddress());
            booking22.setStatus("Cancelled");
            booking22.setCustomer(customer1);
            bookingRepository.save(booking22);

            Booking booking33 = new Booking();
            booking33.setBookingNo("B033");
            booking33.setStartDateTime(startDate33);
            booking33.setTotalPrice(10000);
            booking33.setEndDateTime(endDate33);
            booking33.setDriversInformation("Alice Brown, License: DL321654");
            booking33.setPaymentMethod("Cash");
            booking33.setPickUpLocation(car1.getAddress());
            booking33.setStatus("Cancelled");
            booking33.setCustomer(customer1);
            bookingRepository.save(booking33);
            // Create car bookings
            CarBooking carBooking1 = new CarBooking();
            carBooking1.setCar(car1);
            carBooking1.setBooking(booking1);
            carBookingRepository.save(carBooking1);

            CarBooking carBooking11 = new CarBooking();
            carBooking11.setCar(car2);
            carBooking11.setBooking(booking11);
            carBookingRepository.save(carBooking11);

            CarBooking carBooking2 = new CarBooking();
            carBooking2.setCar(car2);
            carBooking2.setBooking(booking2);
            carBookingRepository.save(carBooking2);

            CarBooking carBooking22 = new CarBooking();
            carBooking22.setCar(car3);
            carBooking22.setBooking(booking22);
            carBookingRepository.save(carBooking22);

            CarBooking carBooking3 = new CarBooking();
            carBooking3.setCar(car3);
            carBooking3.setBooking(booking3);
            carBookingRepository.save(carBooking3);

            CarBooking carBooking33 = new CarBooking();
            carBooking33.setCar(car1);
            carBooking33.setBooking(booking33);
            carBookingRepository.save(carBooking33);

            CarBooking carBooking4 = new CarBooking();
            carBooking4.setCar(car3);
            carBooking4.setBooking(booking4);
            carBookingRepository.save(carBooking4);

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