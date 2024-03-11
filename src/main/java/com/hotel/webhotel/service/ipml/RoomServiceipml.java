package com.hotel.webhotel.service.ipml;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import javax.sql.rowset.serial.SerialBlob;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.hotel.webhotel.exception.InternalServerException;
import com.hotel.webhotel.exception.ResourceNotFoundException;
import com.hotel.webhotel.model.Room;
import com.hotel.webhotel.repository.RoomRepository;
import com.hotel.webhotel.service.IRoomService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoomServiceipml implements IRoomService{

    
    private final RoomRepository roomRepository;

    @Override
    public Room addNewRoom(MultipartFile file, String roomType, BigDecimal roomPrice) throws IOException, SQLException {    
        Room room = new Room();
        room.setRoomtype(roomType);
        room.setRoomPrice(roomPrice);
        if(!file.isEmpty()){
            byte[] photoBytes;    
                photoBytes = file.getBytes();
                Blob photoBlog = new SerialBlob(photoBytes);
                room.setPhoto(photoBlog);      
        }
       return roomRepository.save(room);
    }

    @Override
    public List<String> getAllRoomTypes() {
        return roomRepository.findDistinctRoomTypes();
    }

    @Override
    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    @Override
    public byte[] getRoomPhotoByRoomID(Long roomId) throws SQLException {
        @SuppressWarnings("null")
        Optional<Room> theRoom = roomRepository.findById(roomId);
        if(theRoom.isEmpty()){
            throw new ResourceNotFoundException("Sorry, Room not found!");
        }

        Blob photoBlob = theRoom.get().getPhoto();
        if(photoBlob != null){
            return photoBlob.getBytes(1, (int) photoBlob.length());
        }
        return null;
    }

    @SuppressWarnings("null")
    @Override
    public void deleteRoom(Long roomId) {
        @SuppressWarnings("null")
        Optional<Room> theRoom = roomRepository.findById(roomId);
        if(theRoom.isPresent()){
            roomRepository.deleteById(roomId);
        }
    }

    @SuppressWarnings("null")
    @Override
    public Room updateRoom(Long roomId, String roomType, BigDecimal roomPrice, byte[] photoBytes) {
        @SuppressWarnings("null")
        Room room = roomRepository.findById(roomId).orElseThrow(() -> new ResourceNotFoundException("Room not found"));
        if(roomType != null) room.setRoomtype(roomType);
        if(roomPrice != null) room.setRoomPrice(roomPrice);
        if(photoBytes != null && photoBytes.length > 0){
           try {
                room.setPhoto(new SerialBlob(photoBytes));
           } catch (SQLException ex) {
               throw new InternalServerException("Error updating room");
           }
        }
        return roomRepository.save(room);
    }

    @SuppressWarnings("null")
    @Override
    public Optional<Room> getRoomById(Long roomId) {
        
        return Optional.of(roomRepository.findById(roomId).get());
    }

    @Override
    public List<Room> getAvailibaleRooms(LocalDate checkInDate, LocalDate checkOutDate, String roomType) {
        return roomRepository.findAvailibaleRoomsByDatesAndType(checkInDate, checkOutDate, roomType);
    }
    
}
