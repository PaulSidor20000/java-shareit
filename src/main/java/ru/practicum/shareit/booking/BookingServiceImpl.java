package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.BookState;
import ru.practicum.shareit.booking.model.BookStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exceptions.BookingNotMatchException;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.ItemStorage;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserStorage;

import java.util.List;

import static ru.practicum.shareit.exceptions.ErrorHandler.*;

@Slf4j
@Service("bookingService")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserStorage userStorage;
    private final ItemStorage itemStorage;

    @Override
    @Transactional
    public BookingDto makeBooking(Long bookerId, BookingDto bookingDto) {
        if (!isValid(bookerId, bookingDto) ) {
            throw new BookingNotMatchException("Failed Booking DTO validation");
        }

        Item anItem = itemStorage.findById(bookingDto.getItemId()).orElseThrow(() ->
                new EntityNotFoundException(String.format(FAILED_ITEM_ID, bookingDto.getItemId())));

        if (!anItem.isAvailable()) {
            throw new ValidationException("Booking not available");
        }

        Booking booking = BookingMapper.toBooking(bookingDto);
        booking.setStatus(BookStatus.WAITING);


        booking.setBooker(userStorage.findById(bookerId)
                .orElseThrow(() -> new EntityNotFoundException(String.format(FAILED_USER_ID, bookerId))));

        booking.setItem(itemStorage.findById(bookingDto.getItemId())
                .orElseThrow(() -> new EntityNotFoundException(String.format(FAILED_ITEM_ID, bookingDto.getItemId()))));

        bookingRepository.save(booking);

        return BookingMapper.toBookingDto(bookingRepository.findById(booking.getId()).orElseThrow(() ->
                new EntityNotFoundException(String.format(FAILED_BOOKING_ID, booking.getId()))));
    }

    @Override
    public BookingDto approving(Long ownerId, Long bookingId, boolean isApproved) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new EntityNotFoundException(String.format(FAILED_BOOKING_ID, bookingId)));

        if (booking.getItem().getOwner().getId().equals(ownerId)) {
            if (isApproved) {
                booking.setStatus(BookStatus.APPROVED);
            } else {
                booking.setStatus(BookStatus.REJECTED);
            }
        }
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public BookingDto getBooking(Long bookingId) {
        return null;
    }

    @Override
    public List<BookingDto> getBookerStatistics(Long bookerId, BookState state) {
        return null;
    }

    @Override
    public List<BookingDto> getOwnerStatistics(Long ownerId, BookState state) {
        return null;
    }

    private boolean isValid(Long bookerId, BookingDto bookingDto) {
        if (bookingDto.getStart().isAfter(bookingDto.getEnd())
                || bookingDto.getStart().isEqual(bookingDto.getEnd())) {
            throw new ValidationException("Wrong booking time parameter");
        }
        return userStorage.existsById(bookerId);
          //      && itemStorage.existsById(bookingDto.getItemId());
    }
}
