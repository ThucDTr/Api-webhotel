package com.hotel.webhotel.service.ipml;

import java.util.List;

import org.springframework.stereotype.Service;

import com.hotel.webhotel.exception.InvalidBookingRequestException;
import com.hotel.webhotel.exception.ResourceNotFoundException;
import com.hotel.webhotel.model.BookedRoom;
import com.hotel.webhotel.model.Room;
import com.hotel.webhotel.repository.BookingRepository;
import com.hotel.webhotel.service.IBookingService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookingServiceipml implements IBookingService{

    
    private final BookingRepository bookingRepository;
    
    private final RoomServiceipml roomServiceipml;

    @Override
    public List<BookedRoom> getAllBookings() {
        return bookingRepository.findAll();
    }

    @Override
    public BookedRoom findByBookingConfirmationCode(String confirmationCode) {
        
        return bookingRepository.findByBookingConfirmationCode(confirmationCode)
        .orElseThrow(() -> new ResourceNotFoundException("No booking found with booking code :" + confirmationCode));
    }
    
    @Override
    public String saveBooking(Long roomId, BookedRoom bookingRequest) {
        if(bookingRequest.getCheckOutDate().isBefore(bookingRequest.getCheckInDate())){
            throw new InvalidBookingRequestException("check-in date must come before check-out date");
        }
        Room room = roomServiceipml.getRoomById(roomId).get();
        List<BookedRoom> existingBookings = room.getBookings();
        boolean roomIsvailable = roomIsAvailable(bookingRequest, existingBookings);
         if (roomIsvailable) {
            room.addBooking(bookingRequest);
            bookingRepository.save(bookingRequest);
         }else {
            throw new InvalidBookingRequestException("Room is not available");
         }
        return bookingRequest.getBookingConfirmationCode();
    }

    private boolean roomIsAvailable(BookedRoom bookingRequest, List<BookedRoom> existingBookings) {
        return existingBookings.stream()
                .noneMatch(existingBooking ->
                        bookingRequest.getCheckInDate().equals(existingBooking.getCheckInDate())
                                || bookingRequest.getCheckOutDate().isBefore(existingBooking.getCheckOutDate())
                                || (bookingRequest.getCheckInDate().isAfter(existingBooking.getCheckInDate())
                                && bookingRequest.getCheckInDate().isBefore(existingBooking.getCheckOutDate()))
                                || (bookingRequest.getCheckInDate().isBefore(existingBooking.getCheckInDate())

                                && bookingRequest.getCheckOutDate().equals(existingBooking.getCheckOutDate()))
                                || (bookingRequest.getCheckInDate().isBefore(existingBooking.getCheckInDate())

                                && bookingRequest.getCheckOutDate().isAfter(existingBooking.getCheckOutDate()))

                                || (bookingRequest.getCheckInDate().equals(existingBooking.getCheckOutDate())
                                && bookingRequest.getCheckOutDate().equals(existingBooking.getCheckInDate()))

                                || (bookingRequest.getCheckInDate().equals(existingBooking.getCheckOutDate())
                                && bookingRequest.getCheckOutDate().equals(bookingRequest.getCheckInDate()))
                );
    }

    @SuppressWarnings("null")
    @Override
    public void cancelBooking(Long bookingId) {
        bookingRepository.deleteById(bookingId);
        
    }

    @Override
    public List<BookedRoom> getAllBookingsByRoomId(Long roomId) {
       return bookingRepository.findByRoomId(roomId);
    }
    
}
