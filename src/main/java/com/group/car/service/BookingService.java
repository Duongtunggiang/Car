package com.group.car.service;

import com.group.car.models.Booking;
import com.group.car.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    //  tìm booking theo ID
    public Booking findBookingById(Long bookingId) {
        Optional<Booking> optionalBooking = bookingRepository.findById(bookingId);
        return optionalBooking.orElse(null);
    }

    // lưu một booking
    public Booking saveBooking(Booking booking) {
        return bookingRepository.save(booking);
    }

    //  xóa một booking
    public void deleteBooking(Long bookingId) {
        bookingRepository.deleteById(bookingId);
    }

    // tìm tất cả các booking
    public List<Booking> findAllBookings() {
        return bookingRepository.findAll();
    }

    // cập nhật thông tin booking
    public Booking updateBooking(Long bookingId, Booking updatedBooking) {
        if (bookingRepository.existsById(bookingId)) {
            updatedBooking.setId(bookingId);
            return bookingRepository.save(updatedBooking);
        } else {
            return null;
        }
    }

}

