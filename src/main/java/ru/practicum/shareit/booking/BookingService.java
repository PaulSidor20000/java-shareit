package ru.practicum.shareit.booking;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookState;

import java.util.List;

@Service
public interface BookingService {
    BookingDto makeBooking(Long userId, BookingDto bookingDto);

    BookingDto approving(Long ownerId, Long bookingId, boolean isApproved);

    BookingDto getBooking(Long bookingId, Long user);

    List<BookingDto> getBookerStatistics(Long bookerId, String state);

    List<BookingDto> getOwnerStatistics(Long ownerId, String state);
}
