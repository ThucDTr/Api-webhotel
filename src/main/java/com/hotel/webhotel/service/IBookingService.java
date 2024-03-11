package com.hotel.webhotel.service;

import java.util.List;

import com.hotel.webhotel.model.BookedRoom;

public interface IBookingService {

    List<BookedRoom> getAllBookingsByRoomId(Long roomId);

    void cancelBooking(Long bookingId);

    String saveBooking(Long roomId, BookedRoom bookingRequest);

    BookedRoom findByBookingConfirmationCode(String confirmationCode);

    List<BookedRoom> getAllBookings();
    
}
