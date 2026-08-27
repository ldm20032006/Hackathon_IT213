package demo.btth_session13.loader;

import demo.btth_session13.entity.Room;
import demo.btth_session13.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/**
 * Seeds sample room data on startup (only if the rooms table is empty).
 * Order(1) ensures this runs before DocumentLoader (Order default = Integer.MAX_VALUE).
 */
@Slf4j
@Component
@Order(1)
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final RoomRepository roomRepository;

    @Override
    public void run(ApplicationArguments args) {
        if (roomRepository.count() > 0) {
            log.info("DataInitializer: Rooms already seeded, skipping.");
            return;
        }

        log.info("DataInitializer: Seeding sample rooms...");

        List<Room> sampleRooms = List.of(
                buildRoom("101", "STANDARD",  850_000,  2, "Phòng Standard view thành phố, 1 giường đôi"),
                buildRoom("102", "STANDARD",  850_000,  2, "Phòng Standard view thành phố, 2 giường đơn"),
                buildRoom("201", "DELUXE",  1_200_000,  2, "Phòng Deluxe view biển, 1 giường king"),
                buildRoom("202", "DELUXE",  1_200_000,  3, "Phòng Deluxe view biển, 1 giường king + sofa bed"),
                buildRoom("301", "SUITE",   2_500_000,  4, "Suite cao cấp 2 phòng ngủ, phòng khách riêng, view biển toàn cảnh"),
                buildRoom("302", "SUITE",   2_500_000,  4, "Suite cao cấp 2 phòng ngủ, bồn tắm Jacuzzi, view biển"),
                buildRoom("401", "FAMILY",  1_800_000,  5, "Phòng Family 3 giường đơn, phù hợp gia đình đông người"),
                buildRoom("M01", "STANDARD",  850_000,  2, "Phòng đang bảo trì", Room.RoomStatus.MAINTENANCE)
        );

        roomRepository.saveAll(sampleRooms);
        log.info("DataInitializer: Seeded {} rooms.", sampleRooms.size());
    }

    private Room buildRoom(String number, String type, long price, int capacity, String desc) {
        return buildRoom(number, type, price, capacity, desc, Room.RoomStatus.AVAILABLE);
    }

    private Room buildRoom(String number, String type, long price, int capacity,
                           String desc, Room.RoomStatus status) {
        return Room.builder()
                .roomNumber(number)
                .roomType(type)
                .pricePerNight(BigDecimal.valueOf(price))
                .capacity(capacity)
                .description(desc)
                .status(status)
                .build();
    }
}
