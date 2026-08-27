package demo.btth_session13.tool;

import demo.btth_session13.entity.Booking;
import demo.btth_session13.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Spring AI tool definitions for hotel booking.
 * The AI model calls these methods via Function Calling when a guest
 * wants to book a room through the chat interface.
 */
@Component
@RequiredArgsConstructor
public class BookingTools {

    private static final DateTimeFormatter DT_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final BookingService bookingService;

    /**
     * Books a hotel room on behalf of a customer.
     *
     * @param customerName  full name of the guest
     * @param roomId        ID of the room to book
     * @param checkInTime   check-in date/time in format "yyyy-MM-dd HH:mm"
     * @param checkOutTime  check-out date/time in format "yyyy-MM-dd HH:mm"
     * @return human-readable booking confirmation or error message
     */
    @Tool(description = """
            Đặt phòng khách sạn LotusBay cho khách hàng.
            Tham số:
            - customerName: Họ tên đầy đủ của khách hàng.
            - roomId: ID số nguyên của phòng cần đặt.
            - checkInTime: Thời gian nhận phòng theo định dạng yyyy-MM-dd HH:mm (ví dụ: 2026-09-01 14:00).
            - checkOutTime: Thời gian trả phòng theo định dạng yyyy-MM-dd HH:mm (ví dụ: 2026-09-03 12:00).
            Trả về mã đặt phòng và thông tin xác nhận hoặc thông báo lỗi nếu phòng không còn trống.
            """)
    public String bookRoom(String customerName,
                           Long roomId,
                           String checkInTime,
                           String checkOutTime) {
        try {
            LocalDateTime checkIn  = LocalDateTime.parse(checkInTime,  DT_FORMATTER);
            LocalDateTime checkOut = LocalDateTime.parse(checkOutTime, DT_FORMATTER);

            if (!checkOut.isAfter(checkIn)) {
                return "Lỗi: Thời gian trả phòng phải sau thời gian nhận phòng.";
            }

            Booking booking = bookingService.createBooking(customerName, roomId, checkIn, checkOut);

            return String.format(
                    "Đặt phòng thành công! " +
                    "Mã đặt phòng: %s | " +
                    "Phòng: %s (%s) | " +
                    "Khách: %s | " +
                    "Nhận phòng: %s | " +
                    "Trả phòng: %s",
                    booking.getBookingCode(),
                    booking.getRoom().getRoomNumber(),
                    booking.getRoom().getRoomType(),
                    booking.getCustomerName(),
                    booking.getCheckInTime().format(DT_FORMATTER),
                    booking.getCheckOutTime().format(DT_FORMATTER)
            );

        } catch (DateTimeParseException e) {
            return "Lỗi định dạng thời gian. Vui lòng dùng định dạng yyyy-MM-dd HH:mm (ví dụ: 2026-09-01 14:00).";
        } catch (IllegalArgumentException | IllegalStateException e) {
            return "Không thể đặt phòng: " + e.getMessage();
        } catch (Exception e) {
            return "Đã xảy ra lỗi hệ thống khi đặt phòng. Vui lòng thử lại sau.";
        }
    }
}
