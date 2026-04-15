package com.hotelbooking.repository;

import com.hotelbooking.entity.Booking;
import com.hotelbooking.entity.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<Booking> findAllByOrderByCreatedAtDesc();

    @Query("""
        select (count(b) > 0) from Booking b
        where b.room.id = :roomId
        and b.status = :status
        and b.checkInDate < :checkOutDate
        and b.checkOutDate > :checkInDate
        """)
    boolean existsConflictingBooking(
            @Param("roomId") Long roomId,
            @Param("checkInDate") LocalDate checkInDate,
            @Param("checkOutDate") LocalDate checkOutDate,
            @Param("status") BookingStatus status
    );
}
