package demo.btth_session13.repository;

import demo.btth_session13.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    Optional<Room> findByRoomNumber(String roomNumber);

    List<Room> findByStatus(Room.RoomStatus status);

    /**
     * Returns rooms that have NO confirmed bookings overlapping the requested interval.
     * A booking overlaps when:  bookingCheckIn < requestedCheckOut AND bookingCheckOut > requestedCheckIn
     */
    @Query("""
            SELECT r FROM Room r
            WHERE r.status = 'AVAILABLE'
              AND r.id NOT IN (
                  SELECT b.room.id FROM Booking b
                  WHERE b.status = 'CONFIRMED'
                    AND b.checkInTime  < :checkOut
                    AND b.checkOutTime > :checkIn
              )
            """)
    List<Room> findAvailableRoomsForPeriod(
            @Param("checkIn")  LocalDateTime checkIn,
            @Param("checkOut") LocalDateTime checkOut
    );
}
