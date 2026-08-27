package demo.btth_session13.service;

import demo.btth_session13.entity.Booking;
import demo.btth_session13.entity.Room;
import demo.btth_session13.repository.BookingRepository;
import demo.btth_session13.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;


    @Transactional
    public Booking createBooking(String customerName,
                                 Long roomId,
                                 LocalDateTime checkIn,
                                 LocalDateTime checkOut) {

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Không tìm thấy phòng với ID: " + roomId));

        if (room.getStatus() == Room.RoomStatus.MAINTENANCE) {
            throw new IllegalStateException(
                    "Phòng " + room.getRoomNumber() + " đang bảo trì, không thể đặt.");
        }

        boolean hasOverlap = bookingRepository.existsOverlappingBooking(roomId, checkIn, checkOut);
        if (hasOverlap) {
            throw new IllegalStateException(
                    "Phòng " + room.getRoomNumber() + " đã có đặt chỗ trong khoảng thời gian này.");
        }

        String bookingCode = generateBookingCode();

        Booking booking = Booking.builder()
                .bookingCode(bookingCode)
                .customerName(customerName)
                .room(room)
                .checkInTime(checkIn)
                .checkOutTime(checkOut)
                .status(Booking.BookingStatus.CONFIRMED)
                .build();

        return bookingRepository.save(booking);
    }

    private String generateBookingCode() {
        String datePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String randomPart = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        return "LB-" + datePart + "-" + randomPart;
    }
}
