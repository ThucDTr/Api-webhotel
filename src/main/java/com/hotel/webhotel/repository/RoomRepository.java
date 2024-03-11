package com.hotel.webhotel.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.hotel.webhotel.model.Room;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long>{

    @Query("SELECT DISTINCT r.roomtype FROM Room r")
    List<String> findDistinctRoomTypes();

    @Query(" SELECT r FROM Room r " +
    " WHERE r.roomtype LIKE %:roomType% " +
    " AND r.id NOT IN (" +
    "  SELECT br.room.id FROM BookedRoom br " +
    "  WHERE ((br.checkInDate <= :checkOutDate) AND (br.checkOutDate >= :checkInDate))" +
    ")")
    List<Room> findAvailibaleRoomsByDatesAndType(LocalDate checkInDate, LocalDate checkOutDate, String roomType);
    
}
