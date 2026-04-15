package com.hotelbooking.repository;

import com.hotelbooking.entity.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface HotelRepository extends JpaRepository<Hotel, Long> {

    List<Hotel> findAllByIsActiveTrueOrderByCreatedAtDesc();

    Optional<Hotel> findByIdAndIsActiveTrue(Long id);

    @Query("""
        select h from Hotel h
        where h.isActive = true
        and (:location is null or lower(h.location) like lower(concat('%', :location, '%')))
        and (:amenity is null or lower(h.amenities) like lower(concat('%', :amenity, '%')))
        order by h.createdAt desc
        """)
    List<Hotel> searchActiveHotels(
            @Param("location") String location,
            @Param("amenity") String amenity
    );
}
