package com.hotelbooking.service;

import com.hotelbooking.dto.booking.BookingRequest;
import com.hotelbooking.dto.booking.BookingResponse;
import com.hotelbooking.entity.Booking;
import com.hotelbooking.entity.BookingStatus;
import com.hotelbooking.entity.Room;
import com.hotelbooking.entity.User;
import com.hotelbooking.exception.ResourceNotFoundException;
import com.hotelbooking.exception.UnauthorizedActionException;
import com.hotelbooking.repository.BookingRepository;
import com.hotelbooking.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final RoomService roomService;

    public BookingService(BookingRepository bookingRepository, UserRepository userRepository, RoomService roomService) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.roomService = roomService;
    }

    public BookingResponse createBooking(String userEmail, BookingRequest request) {
        User user = userRepository.findByEmailAndIsActiveTrue(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Active user not found"));

        LocalDate today = LocalDate.now();
        LocalDate maxAllowedCheckIn = today.plusMonths(6);

        if (request.getCheckInDate().isBefore(today)) {
            throw new IllegalArgumentException("Check-in date cannot be in the past");
        }

        if (request.getCheckInDate().isAfter(maxAllowedCheckIn)) {
            throw new IllegalArgumentException("Check-in date must be within the next 6 months");
        }

        if (!request.getCheckOutDate().isAfter(request.getCheckInDate())) {
            throw new IllegalArgumentException("Check-out date must be after check-in date");
        }

        Room room = roomService.getActiveRoomEntity(request.getRoomId());
        boolean available = roomService.isAvailable(room.getId(), request.getCheckInDate(), request.getCheckOutDate());

        if (!available) {
            throw new IllegalArgumentException("Room is not available for the selected date range");
        }

        long nights = ChronoUnit.DAYS.between(request.getCheckInDate(), request.getCheckOutDate());
        BigDecimal totalPrice = room.getPricePerNight().multiply(BigDecimal.valueOf(nights));

        Booking booking = Booking.builder()
                .user(user)
                .room(room)
                .checkInDate(request.getCheckInDate())
                .checkOutDate(request.getCheckOutDate())
                .totalPrice(totalPrice)
                .status(BookingStatus.CONFIRMED)
                .build();

        return toResponse(bookingRepository.save(booking));
    }

    public List<BookingResponse> getMyBookings(String userEmail) {
        User user = userRepository.findByEmailAndIsActiveTrue(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Active user not found"));

        return bookingRepository.findByUserIdOrderByCreatedAtDesc(user.getId()).stream()
                .map(this::toResponse)
                .toList();
    }

    public List<BookingResponse> getAllBookings() {
        return bookingRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::toResponse)
                .toList();
    }

    public BookingResponse cancelBooking(Long bookingId, String userEmail, boolean isAdmin) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));

        if (!isAdmin && !booking.getUser().getEmail().equalsIgnoreCase(userEmail)) {
            throw new UnauthorizedActionException("You are not allowed to cancel this booking");
        }

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new IllegalArgumentException("Booking is already cancelled");
        }

        if (booking.getCheckInDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Cannot cancel booking after check-in date");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        return toResponse(bookingRepository.save(booking));
    }

    private BookingResponse toResponse(Booking booking) {
        return BookingResponse.builder()
                .id(booking.getId())
                .userId(booking.getUser().getId())
                .userName(booking.getUser().getName())
                .roomId(booking.getRoom().getId())
                .roomNumber(booking.getRoom().getRoomNumber())
                .hotelId(booking.getRoom().getHotel().getId())
                .hotelName(booking.getRoom().getHotel().getName())
                .hotelLocation(booking.getRoom().getHotel().getLocation())
                .checkInDate(booking.getCheckInDate())
                .checkOutDate(booking.getCheckOutDate())
                .totalPrice(booking.getTotalPrice())
                .status(booking.getStatus().name())
                .createdAt(booking.getCreatedAt())
                .build();
    }
}
