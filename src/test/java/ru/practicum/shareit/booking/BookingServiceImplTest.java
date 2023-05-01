package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.TestEnvironment;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.BookStatus;
import ru.practicum.shareit.exceptions.*;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest extends TestEnvironment {
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

    @Test
    void makeBookingTest_whenDataValid_thenReturnBookingDto() {
        long bookerId = 1L;
        when(mockUserRepository.existsById(anyLong())).thenReturn(true);
        when(mockItemRepository.findItemByIdAndFetchComments(anyLong())).thenReturn(Optional.of(item));
        when(mockUserRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(mockBookingMapper.merge(user, item, bookingDtoIn)).thenReturn(booking);
        when(mockBookingRepository.save(booking)).thenReturn(booking);
        when(mockBookingMapper.map(booking)).thenReturn(bookingDtoOut);

        BookingDto bookingDtoActual = bookingService.makeBooking(bookerId, bookingDtoIn);

        assertEquals(bookingDtoOut, bookingDtoActual);
        verify(mockBookingRepository).save(booking);
    }

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
        when(mockUserRepository.existsById(anyLong())).thenReturn(true);
        when(mockItemRepository.findItemByIdAndFetchComments(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> bookingService.makeBooking(bookerId, bookingDtoIn));
        verify(mockBookingRepository, never()).save(booking);
    }

    @Test
    void makeBookingTest_whenItemNotAvailable_thenValidationExceptionThrown() {
        long bookerId = 1L;
        item.setAvailable(false);
        when(mockUserRepository.existsById(anyLong())).thenReturn(true);
        when(mockItemRepository.findItemByIdAndFetchComments(anyLong())).thenReturn(Optional.of(item));

        assertThrows(ValidationException.class, () -> bookingService.makeBooking(bookerId, bookingDtoIn));
        verify(mockBookingRepository, never()).save(booking);
    }

    @Test
    void makeBookingTest_whenOwnerIsBooker_thenBookingNotMatchExceptionThrown() {
        long bookerId = 2L;
        when(mockUserRepository.existsById(anyLong())).thenReturn(true);
        when(mockItemRepository.findItemByIdAndFetchComments(anyLong())).thenReturn(Optional.of(item));

        assertThrows(BookingNotMatchException.class, () -> bookingService.makeBooking(bookerId, bookingDtoIn));
        verify(mockBookingRepository, never()).save(booking);
    }

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

        String state = "PAST";
        List<BookingDto> bookingDtosActual = bookingService.getBookerStatistics(bookerId, state, from, size);
        assertEquals(List.of(bookingDtoOut), bookingDtosActual);

        state = "FUTURE";
        bookingDtosActual = bookingService.getBookerStatistics(bookerId, state, from, size);
        assertEquals(List.of(), bookingDtosActual);

        state = "CURRENT";
        bookingDtosActual = bookingService.getBookerStatistics(bookerId, state, from, size);
        assertEquals(List.of(), bookingDtosActual);

        state = "WAITING";
        bookingDtosActual = bookingService.getBookerStatistics(bookerId, state, from, size);
        assertEquals(List.of(), bookingDtosActual);

        state = "REJECTED";
        bookingDtosActual = bookingService.getBookerStatistics(bookerId, state, from, size);
        assertEquals(List.of(), bookingDtosActual);

        state = "ALL";
        bookingDtosActual = bookingService.getBookerStatistics(bookerId, state, from, size);
        assertEquals(List.of(bookingDtoOut), bookingDtosActual);
    }

    @Test
    void getBookerStatisticsTest_whenBookerNotFound_thenEntityNotFoundExceptionThrown() {
        int from = 0;
        int size = 20;
        long bookerId = 2L;
        String state = "PAST";
        PageRequest page = PageRequest.of(from, size);
        when(mockUserRepository.findById(bookerId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> bookingService.getBookerStatistics(bookerId, state, from, size));
        verify(mockUserRepository, never()).findBookingOfOwnerIdAndFetchAllEntities(user, page);
    }

    @Test
    void getBookerStatisticsTest_whenRequestDataNotValid_thenRequestNotValidExceptionThrown() {
        int from = -1;
        int size = 20;
        long bookerId = 2L;
        String state = "PAST";
        PageRequest page = PageRequest.of(0, 1);

        assertThrows(RequestNotValidException.class, () -> bookingService.getBookerStatistics(bookerId, state, from, size));
        verify(mockUserRepository, never()).findBookingsOfUserAndFetchAllEntities(owner, page);
    }

    @Test
    void getBookerStatisticsTest_whenStateNotValid_thenUnknownStateExceptionThrown() {
        int from = 0;
        int size = 20;
        long bookerId = 2L;
        String state = "UNKNOWN";
        PageRequest page = PageRequest.of(from, size);
        when(mockUserRepository.findById(bookerId)).thenReturn(Optional.of(user));
        when(mockUserRepository.findBookingsOfUserAndFetchAllEntities(user, page)).thenReturn(List.of(booking));

        assertThrows(UnknownStateException.class, () -> bookingService.getBookerStatistics(bookerId, state, from, size));
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

        String state = "PAST";
        List<BookingDto> bookingDtosActual = bookingService.getOwnerStatistics(ownerId, state, from, size);
        assertEquals(List.of(bookingDtoOut), bookingDtosActual);

        state = "FUTURE";
        bookingDtosActual = bookingService.getOwnerStatistics(ownerId, state, from, size);
        assertEquals(List.of(), bookingDtosActual);

        state = "CURRENT";
        bookingDtosActual = bookingService.getOwnerStatistics(ownerId, state, from, size);
        assertEquals(List.of(), bookingDtosActual);

        state = "WAITING";
        bookingDtosActual = bookingService.getOwnerStatistics(ownerId, state, from, size);
        assertEquals(List.of(), bookingDtosActual);

        state = "REJECTED";
        bookingDtosActual = bookingService.getOwnerStatistics(ownerId, state, from, size);
        assertEquals(List.of(), bookingDtosActual);

        state = "ALL";
        bookingDtosActual = bookingService.getOwnerStatistics(ownerId, state, from, size);
        assertEquals(List.of(bookingDtoOut), bookingDtosActual);
    }

    @Test
    void getOwnerStatisticsTest_whenOwnerNotFound_thenEntityNotFoundExceptionThrown() {
        int from = 0;
        int size = 20;
        long ownerId = 2L;
        String state = "PAST";
        PageRequest page = PageRequest.of(from, size);
        when(mockUserRepository.findById(ownerId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> bookingService.getOwnerStatistics(ownerId, state, from, size));
        verify(mockUserRepository, never()).findBookingOfOwnerIdAndFetchAllEntities(owner, page);
    }

    @Test
    void getOwnerStatisticsTest_whenRequestDataNotValid_thenRequestNotValidExceptionThrown() {
        int from = -1;
        int size = 20;
        long ownerId = 2L;
        String state = "PAST";
        PageRequest page = PageRequest.of(0, 1);

        assertThrows(RequestNotValidException.class, () -> bookingService.getOwnerStatistics(ownerId, state, from, size));
        verify(mockUserRepository, never()).findBookingOfOwnerIdAndFetchAllEntities(owner, page);
    }

    @Test
    void getOwnerStatisticsTest_whenStateNotValid_thenUnknownStateExceptionThrown() {
        int from = 0;
        int size = 20;
        long ownerId = 2L;
        String state = "UNKNOWN";
        PageRequest page = PageRequest.of(from, size);
        when(mockUserRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(mockUserRepository.findBookingOfOwnerIdAndFetchAllEntities(owner, page)).thenReturn(List.of(booking));

        assertThrows(UnknownStateException.class, () -> bookingService.getOwnerStatistics(ownerId, state, from, size));
    }

}