package demo.btth_session13.tool;

import demo.btth_session13.entity.Room;
import demo.btth_session13.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * MCP Server tools exposed to external MCP clients (e.g. Claude Desktop, MCP Inspector).
 * These tools are registered automatically via Spring AI MCP auto-configuration
 * when the bean is present in the context.
 */
@Component
@RequiredArgsConstructor
public class McpHotelTools {

    private final RoomService roomService;

    /**
     * Returns a list of all rooms currently in AVAILABLE status.
     */
    @Tool(description = """
            Lấy danh sách tất cả các phòng khách sạn LotusBay đang ở trạng thái còn trống (AVAILABLE).
            Trả về danh sách gồm ID, số phòng, loại phòng, giá mỗi đêm, sức chứa và mô tả.
            Dùng để kiểm tra phòng còn trống trước khi đặt hoặc để hiển thị cho đối tác OTA.
            """)
    public String getAvailableRooms() {
        List<Room> rooms = roomService.getAvailableRooms();
        if (rooms.isEmpty()) {
            return "Hiện tại không có phòng nào còn trống.";
        }
        return rooms.stream()
                .map(r -> String.format(
                        "ID: %d | Phòng: %s | Loại: %s | Giá/đêm: %,.0f VNĐ | Sức chứa: %d người | %s",
                        r.getId(),
                        r.getRoomNumber(),
                        r.getRoomType(),
                        r.getPricePerNight(),
                        r.getCapacity(),
                        r.getDescription() != null ? r.getDescription() : ""))
                .collect(Collectors.joining("\n"));
    }

    /**
     * Creates a new room in the hotel system.
     *
     * @param roomNumber     unique room number/code (e.g. "101", "P2-Suite")
     * @param roomType       type of room (e.g. STANDARD, DELUXE, SUITE)
     * @param pricePerNight  nightly rate in VND
     * @param capacity       maximum number of guests
     * @param description    optional description of the room
     * @return confirmation string with the created room's ID
     */
    @Tool(description = """
            Tạo mới một phòng khách sạn trong hệ thống LotusBay.
            Tham số:
            - roomNumber: Mã/số phòng duy nhất (ví dụ: "101", "NT-SUITE-01").
            - roomType: Loại phòng (ví dụ: STANDARD, DELUXE, SUITE, FAMILY).
            - pricePerNight: Giá mỗi đêm tính theo VNĐ (số thực, ví dụ: 1500000).
            - capacity: Số khách tối đa (số nguyên, ví dụ: 2).
            - description: Mô tả phòng (có thể để trống).
            Phòng mới sẽ được tạo với trạng thái AVAILABLE.
            Trả về thông tin phòng vừa tạo bao gồm ID.
            """)
    public String createRoom(String roomNumber,
                             String roomType,
                             double pricePerNight,
                             int capacity,
                             String description) {
        try {
            Room created = roomService.createRoom(
                    roomNumber,
                    roomType,
                    BigDecimal.valueOf(pricePerNight),
                    capacity,
                    description);

            return String.format(
                    "Tạo phòng thành công! ID: %d | Số phòng: %s | Loại: %s | Giá/đêm: %,.0f VNĐ | Sức chứa: %d người",
                    created.getId(),
                    created.getRoomNumber(),
                    created.getRoomType(),
                    created.getPricePerNight(),
                    created.getCapacity());
        } catch (Exception e) {
            return "Lỗi khi tạo phòng: " + e.getMessage();
        }
    }
}
