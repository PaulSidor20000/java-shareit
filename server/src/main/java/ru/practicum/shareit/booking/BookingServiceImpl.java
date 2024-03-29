package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
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

@Service("bookingService")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingMapper bookingMapper;

    @Override
    @Transactional
    public BookingDto makeBooking(Long bookerId, BookingDto bookingDto) {
        User booker = userRepository.findById(bookerId)
                .orElseThrow(() -> new EntityNotFoundException(String.format(FAILED_USER_ID, bookerId)));
        Item item = itemRepository.findItemByIdAndFetchComments(bookingDto.getItemId()).orElseThrow(() ->
                new EntityNotFoundException(String.format(FAILED_ITEM_ID, bookingDto.getItemId())));

        if (!item.isAvailable()) {
            throw new ValidationException("Booking not available");
        }

        if (item.getOwner().getId().equals(bookerId)) {
            throw new BookingNotMatchException("Failed to book item by owner");
        }

        Booking booking = bookingMapper.merge(booker, item, bookingDto);

        return bookingMapper.map(bookingRepository.save(booking));
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

        return bookingMapper.map(booking);
    }

    @Override
    public BookingDto getBooking(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findBookingByIdAndFetchAllEntities(bookingId).orElseThrow(() ->
                new EntityNotFoundException(String.format(FAILED_BOOKING_ID, bookingId)));

        if (booking.getItem().getOwner().getId().equals(userId) || booking.getBooker().getId().equals(userId)) {
            return bookingMapper.map(booking);
        }

        throw new EntityNotFoundException(String.format(FAILED_USER_ID, userId));
    }

    @Override
    public List<BookingDto> getBookerStatistics(Long bookerId, BookState state, PageRequest page) {
        User booker = userRepository.findById(bookerId)
                .orElseThrow(() -> new EntityNotFoundException(String.format(FAILED_USER_ID, bookerId)));

        Collection<Booking> bookings = userRepository.findBookingsOfUserAndFetchAllEntities(booker, page);

        return getBookingStatistics(bookings, state);
    }

    @Override
    public List<BookingDto> getOwnerStatistics(Long ownerId, BookState state, PageRequest page) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new EntityNotFoundException(String.format(FAILED_USER_ID, ownerId)));

        Collection<Booking> bookings = userRepository.findBookingOfOwnerIdAndFetchAllEntities(owner, page);

        return getBookingStatistics(bookings, state);
    }

    private List<BookingDto> getBookingStatistics(Collection<Booking> bookings, BookState state) {
        LocalDateTime now = LocalDateTime.now();

        switch (state) {
            case PAST:
                return bookings.stream()
                        .filter(booking -> booking.getEnd().isBefore(now))
                        .sorted(Comparator.comparing(Booking::getStart).reversed())
                        .map(bookingMapper::map)
                        .collect(Collectors.toCollection(LinkedList::new));
            case CURRENT:
                return bookings.stream()
                        .filter(booking -> booking.getStart().isBefore(now)
                                && booking.getEnd().isAfter(now))
                        .sorted(Comparator.comparing(Booking::getEnd).reversed())
                        .map(bookingMapper::map)
                        .collect(Collectors.toCollection(LinkedList::new));
            case FUTURE:
                return bookings.stream()
                        .filter(booking -> booking.getEnd().isAfter(now))
                        .sorted(Comparator.comparing(Booking::getStart).reversed())
                        .map(bookingMapper::map)
                        .collect(Collectors.toCollection(LinkedList::new));
            case WAITING:
                return bookings.stream()
                        .filter(booking -> booking.getStatus() == BookStatus.WAITING)
                        .sorted(Comparator.comparing(Booking::getStart).reversed())
                        .map(bookingMapper::map)
                        .collect(Collectors.toCollection(LinkedList::new));
            case REJECTED:
                return bookings.stream()
                        .filter(booking -> booking.getStatus() == BookStatus.REJECTED)
                        .sorted(Comparator.comparing(Booking::getStart).reversed())
                        .map(bookingMapper::map)
                        .collect(Collectors.toCollection(LinkedList::new));
            default:
                return bookings.stream()
                        .sorted(Comparator.comparing(Booking::getStart).reversed())
                        .map(bookingMapper::map)
                        .collect(Collectors.toCollection(LinkedList::new));
        }
    }

}
