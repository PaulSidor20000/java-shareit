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
import ru.practicum.shareit.exceptions.UnknownStateException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.exceptions.ErrorHandler.*;

@Slf4j
@Service("bookingService")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public BookingDto makeBooking(Long bookerId, BookingDto bookingDto) {
        if (!isValid(bookerId, bookingDto)) {
            throw new BookingNotMatchException("Failed Booking DTO validation");
        }

        Item item = itemRepository.findById(bookingDto.getItemId()).orElseThrow(() ->
                new EntityNotFoundException(String.format(FAILED_ITEM_ID, bookingDto.getItemId())));

        if (!item.isAvailable()) {
            throw new ValidationException("Booking not available");
        }

        if (item.getOwner().getId().equals(bookerId)) {
            throw new BookingNotMatchException("Failed to book item by owner");
        }

        User booker = userRepository.findById(bookerId)
                .orElseThrow(() -> new EntityNotFoundException(String.format(FAILED_USER_ID, bookerId)));
        Booking booking = BookingMapper.toBooking(bookingDto);

        booking.setStatus(BookStatus.WAITING);
        booking.setBooker(booker);
        booking.setItem(item);

        bookingRepository.save(booking);

        return BookingMapper.toBookingDto(bookingRepository.findById(booking.getId()).orElseThrow(() ->
                new EntityNotFoundException(String.format(FAILED_BOOKING_ID, booking.getId()))));
    }

    @Override
    @Transactional
    public BookingDto approving(Long ownerId, Long bookingId, boolean isApproved) {
        Booking booking = bookingRepository.findBookingByIdAndFetchAllEntities(bookingId).orElseThrow(() ->
                new EntityNotFoundException(String.format(FAILED_BOOKING_ID, bookingId)));

        if (booking.getStatus() != BookStatus.WAITING) {
            throw new IllegalArgumentException("Rejected for approving operation");
        }

        if (booking.getItem().getOwner().getId().equals(ownerId)) {
            if (isApproved) {
                booking.setStatus(BookStatus.APPROVED);
            } else {
                booking.setStatus(BookStatus.REJECTED);
            }
        } else {
            throw new EntityNotFoundException(String.format(FAILED_OWNER_ID, ownerId));
        }

        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public BookingDto getBooking(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findBookingByIdAndFetchAllEntities(bookingId).orElseThrow(() ->
                new EntityNotFoundException(String.format(FAILED_BOOKING_ID, bookingId)));

        if (booking.getItem().getOwner().getId().equals(userId) || booking.getBooker().getId().equals(userId)) {
            return BookingMapper.toBookingDto(booking);
        }

        throw new EntityNotFoundException(String.format(FAILED_USER_ID, userId));
    }

    @Override
    public List<BookingDto> getBookerStatistics(Long bookerId, String requestState) {
        User booker = userRepository.findById(bookerId)
                .orElseThrow(() -> new EntityNotFoundException(String.format(FAILED_USER_ID, bookerId)));

        Collection<Long> bookingIds = booker.getBookings().stream()
                .map(Booking::getId)
                .collect(Collectors.toSet());

        Collection<Booking> bookings = bookingRepository.findBookingsAndFetchAllEntities(bookingIds);

        return getBookingStatistics(bookings, requestState);
    }

    @Override
    public List<BookingDto> getOwnerStatistics(Long ownerId, String requestState) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new EntityNotFoundException(String.format(FAILED_USER_ID, ownerId)));

        Collection<Long> bookingIds = owner.getItems().stream()
                .flatMap(item -> item.getBookings().stream())
                .map(Booking::getId)
                .collect(Collectors.toSet());

        Collection<Booking> bookings = bookingRepository.findBookingsAndFetchAllEntities(bookingIds);

        return getBookingStatistics(bookings, requestState);
    }

    private List<BookingDto> getBookingStatistics(Collection<Booking> bookings, String requestState) {
        LocalDateTime now = LocalDateTime.now();

        switch (getState(requestState)) {
            case PAST:
                return bookings.stream()
                        .filter(booking -> booking.getEnd().isBefore(now))
                        .sorted(Comparator.comparing(Booking::getStart).reversed())
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toCollection(LinkedList::new));
            case CURRENT:
                return bookings.stream()
                        .filter(booking -> booking.getStart().isBefore(now)
                                && booking.getEnd().isAfter(now))
                        .sorted(Comparator.comparing(Booking::getEnd).reversed())
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toCollection(LinkedList::new));
            case FUTURE:
                return bookings.stream()
                        .filter(booking -> booking.getEnd().isAfter(now))
                        .sorted(Comparator.comparing(Booking::getStart).reversed())
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toCollection(LinkedList::new));
            case WAITING:
                return bookings.stream()
                        .filter(booking -> booking.getStatus() == BookStatus.WAITING)
                        .sorted(Comparator.comparing(Booking::getStart).reversed())
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toCollection(LinkedList::new));
            case REJECTED:
                return bookings.stream()
                        .filter(booking -> booking.getStatus() == BookStatus.REJECTED)
                        .sorted(Comparator.comparing(Booking::getStart).reversed())
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toCollection(LinkedList::new));
            default:
                return bookings.stream()
                        .sorted(Comparator.comparing(Booking::getStart).reversed())
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toCollection(LinkedList::new));
        }
    }


    private BookState getState(String requestState) {
        try {
            return BookState.valueOf(requestState);
        } catch (IllegalArgumentException e) {
            throw new UnknownStateException(String.format(UNKNOWN_STATE, requestState));
        }
    }

    private boolean isValid(Long bookerId, BookingDto bookingDto) {
        if (bookingDto.getStart().isAfter(bookingDto.getEnd())
                || bookingDto.getStart().isEqual(bookingDto.getEnd())) {
            throw new ValidationException("Wrong booking time parameter");
        }
        return userRepository.existsById(bookerId);
    }

}
