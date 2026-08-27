package demo.btth_session13.service;

import demo.btth_session13.entity.Room;
import demo.btth_session13.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;

    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    public List<Room> getAvailableRooms() {
        return roomRepository.findByStatus(Room.RoomStatus.AVAILABLE);
    }

    public List<Room> getAvailableRoomsForPeriod(LocalDateTime checkIn, LocalDateTime checkOut) {
        return roomRepository.findAvailableRoomsForPeriod(checkIn, checkOut);
    }

    public Optional<Room> findById(Long id) {
        return roomRepository.findById(id);
    }

    public Optional<Room> findByRoomNumber(String roomNumber) {
        return roomRepository.findByRoomNumber(roomNumber);
    }

    @Transactional
    public Room createRoom(String roomNumber, String roomType, BigDecimal pricePerNight,
                           int capacity, String description) {
        Room room = Room.builder()
                .roomNumber(roomNumber)
                .roomType(roomType)
                .pricePerNight(pricePerNight)
                .capacity(capacity)
                .description(description)
                .status(Room.RoomStatus.AVAILABLE)
                .build();
        return roomRepository.save(room);
    }

    @Transactional
    public Room updateRoomStatus(Long roomId, Room.RoomStatus newStatus) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Room not found: " + roomId));
        room.setStatus(newStatus);
        return roomRepository.save(room);
    }
}
