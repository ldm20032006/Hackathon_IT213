package demo.btth_session13.repository;

import demo.btth_session13.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    Optional<Booking> findByBookingCode(String bookingCode);

    List<Booking> findByCustomerNameIgnoreCase(String customerName);

    /**
     * Checks whether a specific room has any CONFIRMED booking overlapping
     * the given time interval (used before creating a new booking).
     */
    @Query("""
            SELECT COUNT(b) > 0 FROM Booking b
            WHERE b.room.id     = :roomId
              AND b.status      = 'CONFIRMED'
              AND b.checkInTime  < :checkOut
              AND b.checkOutTime > :checkIn
            """)
    boolean existsOverlappingBooking(
            @Param("roomId")   Long roomId,
            @Param("checkIn")  LocalDateTime checkIn,
            @Param("checkOut") LocalDateTime checkOut
    );
}
