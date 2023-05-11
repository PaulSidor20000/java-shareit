package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.BookState;
import ru.practicum.shareit.booking.model.BookStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exceptions.BookingNotMatchException;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.exceptions.RequestNotValidException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {
    @Mock
    private BookingRepository mockBookingRepository;
    @Mock
    private UserRepository mockUserRepository;
    @Mock
    private ItemRepository mockItemRepository;
    @Mock
    private BookingMapper mockBookingMapper;
    @InjectMocks
    private BookingServiceImpl bookingService;

    private User user, owner;
    private Item item;
    private Booking booking;
    private BookingDto bookingDtoIn, bookingDtoOut;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("John");
        user.setEmail("john@mail.com");

        owner = new User();
        owner.setId(2L);
        owner.setName("Owner");
        owner.setEmail("owner@mail.ru");

        item = new Item();
        item.setId(1L);
        item.setName("Item1");
        item.setDescription("Item1 Description");
        item.setAvailable(true);
        item.setOwner(owner);

        booking = new Booking();
        booking.setStatus(BookStatus.APPROVED);
        booking.setStart(LocalDateTime.now().minusDays(2));
        booking.setEnd(LocalDateTime.now().minusDays(1));
        booking.setItem(item);
        booking.setBooker(user);

        bookingDtoIn = new BookingDto();
        bookingDtoIn.setItemId(1L);
        bookingDtoIn.setStart(LocalDateTime.now().minusDays(2));
        bookingDtoIn.setEnd(LocalDateTime.now().minusDays(1));

        bookingDtoOut = new BookingDto();
        bookingDtoOut.setItemId(1L);
        bookingDtoOut.setStart(LocalDateTime.now().minusDays(2));
        bookingDtoOut.setEnd(LocalDateTime.now().minusDays(1));
        bookingDtoOut.setStatus(BookStatus.APPROVED);
    }

    @Test
    void makeBookingTest_whenDataValid_thenReturnBookingDto() {
        long bookerId = 1L;
        when(mockUserRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(mockItemRepository.findItemByIdAndFetchComments(anyLong())).thenReturn(Optional.of(item));
        when(mockBookingMapper.merge(user, item, bookingDtoIn)).thenReturn(booking);
        when(mockBookingRepository.save(booking)).thenReturn(booking);
        when(mockBookingMapper.map(booking)).thenReturn(new BookingDto());

        BookingDto bookingDtoActual = bookingService.makeBooking(bookerId, bookingDtoIn);

        assertEquals(new BookingDto(), bookingDtoActual);
        verify(mockBookingRepository).save(booking);
    }

    @Disabled("Validation was removed from this module")
    @Test
    void makeBookingTest_whenBookerNotFound_thenBookingNotMatchExceptionThrown() {
        long bookerId = 1L;
        when(mockUserRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(BookingNotMatchException.class, () -> bookingService.makeBooking(bookerId, bookingDtoIn));
        verify(mockBookingRepository, never()).save(booking);
    }

    @Test
    void makeBookingTest_whenItemNotFound_thenEntityNotFoundExceptionThrown() {
        long bookerId = 1L;
        when(mockUserRepository.findById(anyLong())).thenReturn(Optional.of(new User()));
        when(mockItemRepository.findItemByIdAndFetchComments(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> bookingService.makeBooking(bookerId, bookingDtoIn));
        verify(mockBookingRepository, never()).save(booking);
    }

    @Test
    void makeBookingTest_whenItemNotAvailable_thenValidationExceptionThrown() {
        long bookerId = 1L;
        item.setAvailable(false);
        when(mockUserRepository.findById(anyLong())).thenReturn(Optional.of(new User()));
        when(mockItemRepository.findItemByIdAndFetchComments(anyLong())).thenReturn(Optional.of(item));

        assertThrows(ValidationException.class, () -> bookingService.makeBooking(bookerId, bookingDtoIn));
        verify(mockBookingRepository, never()).save(booking);
    }

    @Test
    void makeBookingTest_whenOwnerIsBooker_thenBookingNotMatchExceptionThrown() {
        long bookerId = 2L;
        when(mockUserRepository.findById(anyLong())).thenReturn(Optional.of(new User()));
        when(mockItemRepository.findItemByIdAndFetchComments(anyLong())).thenReturn(Optional.of(item));

        assertThrows(BookingNotMatchException.class, () -> bookingService.makeBooking(bookerId, bookingDtoIn));
        verify(mockBookingRepository, never()).save(booking);
    }

    @Disabled("Validation was removed from this module")
    @Test
    void makeBookingTest_whenTimeDataNotValid_thenValidationExceptionThrown() {
        long bookerId = 1L;
        bookingDtoIn.setStart(LocalDateTime.now());

        assertThrows(ValidationException.class, () -> bookingService.makeBooking(bookerId, bookingDtoIn));
        verify(mockBookingRepository, never()).save(booking);
    }

    @Test
    void approvingTest_whenInvoke_thenChangeAvailabilityAndReturnBookingDto() {
        long bookingId = 1L;
        long ownerId = 2L;
        booking.setStatus(BookStatus.WAITING);
        when(mockBookingRepository.findBookingByIdAndFetchAllEntities(bookingId)).thenReturn(Optional.of(booking));
        when(mockBookingMapper.map(booking)).thenReturn(bookingDtoOut);

        BookingDto bookingDtoActual = bookingService.approving(ownerId, bookingId, true);

        assertEquals(bookingDtoOut, bookingDtoActual);

        booking.setStatus(BookStatus.WAITING);
        bookingDtoOut.setStatus(BookStatus.REJECTED);
        bookingDtoActual = bookingService.approving(ownerId, bookingId, false);

        assertEquals(bookingDtoOut, bookingDtoActual);
    }

    @Test
    void approvingTest_whenBookingNotFound_thenEntityNotFoundExceptionThrown() {
        long bookingId = 1L;
        long ownerId = 2L;
        booking.setStatus(BookStatus.WAITING);
        when(mockBookingRepository.findBookingByIdAndFetchAllEntities(bookingId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> bookingService.approving(ownerId, bookingId, true));
    }

    @Test
    void approvingTest_whenBookingStatusNotWaiting_thenIllegalArgumentExceptionThrown() {
        long bookingId = 1L;
        long ownerId = 2L;
        booking.setStatus(BookStatus.APPROVED);
        when(mockBookingRepository.findBookingByIdAndFetchAllEntities(bookingId)).thenReturn(Optional.of(booking));

        assertThrows(IllegalArgumentException.class, () -> bookingService.approving(ownerId, bookingId, true));
    }

    @Test
    void approvingTest_whenUserNotOwner_thenEntityNotFoundExceptionThrown() {
        long bookingId = 1L;
        long ownerId = 1L;
        booking.setStatus(BookStatus.WAITING);
        when(mockBookingRepository.findBookingByIdAndFetchAllEntities(bookingId)).thenReturn(Optional.of(booking));

        assertThrows(EntityNotFoundException.class, () -> bookingService.approving(ownerId, bookingId, true));
    }

    @Test
    void getBookingTest_whenBookerOrOwnerGettingTheirBooking_thenReturnBookingDto() {
        long bookingId = 1L;
        long userId = 1L;
        when(mockBookingRepository.findBookingByIdAndFetchAllEntities(bookingId)).thenReturn(Optional.of(booking));
        when(mockBookingMapper.map(booking)).thenReturn(bookingDtoOut);

        BookingDto bookingDtoActual = bookingService.getBooking(bookingId, userId);

        assertEquals(bookingDtoOut, bookingDtoActual);

        userId = 2L;
        bookingDtoActual = bookingService.getBooking(bookingId, userId);
        assertEquals(bookingDtoOut, bookingDtoActual);
    }

    @Test
    void getBookingTest_whenBookingNotFound_thenEntityNotFoundExceptionThrown() {
        long bookingId = 1L;
        long userId = 1L;
        when(mockBookingRepository.findBookingByIdAndFetchAllEntities(bookingId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> bookingService.getBooking(bookingId, userId));
    }

    @Test
    void getBookingTest_whenGettingBookingNotBookerOrOwner_thenEntityNotFoundExceptionThrown() {
        long bookingId = 1L;
        long userId = 3L;
        when(mockBookingRepository.findBookingByIdAndFetchAllEntities(bookingId)).thenReturn(Optional.of(booking));

        assertThrows(EntityNotFoundException.class, () -> bookingService.getBooking(bookingId, userId));
    }

    @Test
    void getBookerStatisticsTest_whenInvoke_thenReturnListBookingDto() {
        int from = 0;
        int size = 20;
        long bookerId = 1L;
        PageRequest page = PageRequest.of(from, size);
        when(mockUserRepository.findById(bookerId)).thenReturn(Optional.of(user));
        when(mockUserRepository.findBookingsOfUserAndFetchAllEntities(user, page)).thenReturn(List.of(booking));
        when(mockBookingMapper.map(booking)).thenReturn(bookingDtoOut);

        BookState state = BookState.valueOf("PAST");
        List<BookingDto> bookingDtosActual = bookingService.getBookerStatistics(bookerId, state, page);
        assertEquals(List.of(bookingDtoOut), bookingDtosActual);

        state = BookState.valueOf("FUTURE");
        bookingDtosActual = bookingService.getBookerStatistics(bookerId, state, page);
        assertEquals(List.of(), bookingDtosActual);

        state = BookState.valueOf("CURRENT");
        bookingDtosActual = bookingService.getBookerStatistics(bookerId, state, page);
        assertEquals(List.of(), bookingDtosActual);

        state = BookState.valueOf("WAITING");
        bookingDtosActual = bookingService.getBookerStatistics(bookerId, state, page);
        assertEquals(List.of(), bookingDtosActual);

        state = BookState.valueOf("REJECTED");
        bookingDtosActual = bookingService.getBookerStatistics(bookerId, state, page);
        assertEquals(List.of(), bookingDtosActual);

        state = BookState.valueOf("ALL");
        bookingDtosActual = bookingService.getBookerStatistics(bookerId, state, page);
        assertEquals(List.of(bookingDtoOut), bookingDtosActual);
    }

    @Test
    void getBookerStatisticsTest_whenBookerNotFound_thenEntityNotFoundExceptionThrown() {
        int from = 0;
        int size = 20;
        long bookerId = 2L;
        BookState state = BookState.valueOf("PAST");
        PageRequest page = PageRequest.of(from, size);
        when(mockUserRepository.findById(bookerId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> bookingService.getBookerStatistics(bookerId, state, page));
        verify(mockUserRepository, never()).findBookingOfOwnerIdAndFetchAllEntities(user, page);
    }

    @Disabled("Validation was removed from this module")
    @Test
    void getBookerStatisticsTest_whenRequestDataNotValid_thenRequestNotValidExceptionThrown() {
        int from = -1;
        int size = 20;
        long bookerId = 2L;
        BookState state = BookState.valueOf("PAST");
        PageRequest page = PageRequest.of(from, size);

        assertThrows(RequestNotValidException.class, () -> bookingService.getBookerStatistics(bookerId, state, page));
        verify(mockUserRepository, never()).findBookingsOfUserAndFetchAllEntities(owner, page);
    }

    @Disabled("Validation was removed from this module")
    @Test
    void getBookerStatisticsTest_whenStateNotValid_thenUnknownStateExceptionThrown() {
        int from = 0;
        int size = 20;
        long bookerId = 2L;
        BookState state = BookState.valueOf("UNKNOWN");
        PageRequest page = PageRequest.of(from, size);
        when(mockUserRepository.findById(bookerId)).thenReturn(Optional.of(user));
        when(mockUserRepository.findBookingsOfUserAndFetchAllEntities(user, page)).thenReturn(List.of(booking));

        assertThrows(ValidationException.class, () -> bookingService.getBookerStatistics(bookerId, state, page));
    }

    @Test
    void getOwnerStatisticsTest_whenInvoke_thenReturnListBookingDto() {
        int from = 0;
        int size = 20;
        long ownerId = 2L;
        PageRequest page = PageRequest.of(from, size);
        when(mockUserRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(mockUserRepository.findBookingOfOwnerIdAndFetchAllEntities(owner, page)).thenReturn(List.of(booking));
        when(mockBookingMapper.map(booking)).thenReturn(bookingDtoOut);

        BookState state = BookState.valueOf("PAST");
        List<BookingDto> bookingDtosActual = bookingService.getOwnerStatistics(ownerId, state, page);
        assertEquals(List.of(bookingDtoOut), bookingDtosActual);

        state = BookState.valueOf("FUTURE");
        bookingDtosActual = bookingService.getOwnerStatistics(ownerId, state, page);
        assertEquals(List.of(), bookingDtosActual);

        state = BookState.valueOf("CURRENT");
        bookingDtosActual = bookingService.getOwnerStatistics(ownerId, state, page);
        assertEquals(List.of(), bookingDtosActual);

        state = BookState.valueOf("WAITING");
        bookingDtosActual = bookingService.getOwnerStatistics(ownerId, state, page);
        assertEquals(List.of(), bookingDtosActual);

        state = BookState.valueOf("REJECTED");
        bookingDtosActual = bookingService.getOwnerStatistics(ownerId, state, page);
        assertEquals(List.of(), bookingDtosActual);

        state = BookState.valueOf("ALL");
        bookingDtosActual = bookingService.getOwnerStatistics(ownerId, state, page);
        assertEquals(List.of(bookingDtoOut), bookingDtosActual);
    }

    @Test
    void getOwnerStatisticsTest_whenOwnerNotFound_thenEntityNotFoundExceptionThrown() {
        int from = 0;
        int size = 20;
        long ownerId = 2L;
        BookState state = BookState.valueOf("PAST");
        PageRequest page = PageRequest.of(from, size);
        when(mockUserRepository.findById(ownerId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> bookingService.getOwnerStatistics(ownerId, state, page));
        verify(mockUserRepository, never()).findBookingOfOwnerIdAndFetchAllEntities(owner, page);
    }

    @Disabled("Validation was removed from this module")
    @Test
    void getOwnerStatisticsTest_whenRequestDataNotValid_thenRequestNotValidExceptionThrown() {
        int from = -1;
        int size = 20;
        long ownerId = 2L;
        BookState state = BookState.valueOf("PAST");
        PageRequest page = PageRequest.of(from, size);

        assertThrows(RequestNotValidException.class, () -> bookingService.getOwnerStatistics(ownerId, state, page));
        verify(mockUserRepository, never()).findBookingOfOwnerIdAndFetchAllEntities(owner, page);
    }

    @Disabled("Validation was removed from this module")
    @Test
    void getOwnerStatisticsTest_whenStateNotValid_thenUnknownStateExceptionThrown() {
        int from = 0;
        int size = 20;
        long ownerId = 2L;
        BookState state = BookState.valueOf("UNKNOWN");
        PageRequest page = PageRequest.of(from, size);
        when(mockUserRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(mockUserRepository.findBookingOfOwnerIdAndFetchAllEntities(owner, page)).thenReturn(List.of(booking));

        assertThrows(ValidationException.class, () -> bookingService.getOwnerStatistics(ownerId, state, page));
    }

}