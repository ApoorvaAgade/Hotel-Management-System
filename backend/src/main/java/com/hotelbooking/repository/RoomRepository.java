package com.hotelbooking.repository;

import com.hotelbooking.entity.Room;
import com.hotelbooking.entity.RoomCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Long> {

    List<Room> findByHotelIdAndIsActiveTrueOrderByCreatedAtDesc(Long hotelId);

    Optional<Room> findByIdAndIsActiveTrue(Long id);

    @Query("""
        select r from Room r
        join r.hotel h
        where r.isActive = true
        and h.isActive = true
        and (:location is null or lower(h.location) like lower(concat('%', :location, '%')))
        and (:minPrice is null or r.pricePerNight >= :minPrice)
        and (:maxPrice is null or r.pricePerNight <= :maxPrice)
        and (:category is null or r.category = :category)
        order by r.createdAt desc
        """)
    List<Room> searchActiveRooms(
            @Param("location") String location,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("category") RoomCategory category
    );

    boolean existsByHotelIdAndRoomNumberIgnoreCaseAndIsActiveTrue(Long hotelId, String roomNumber);
}
