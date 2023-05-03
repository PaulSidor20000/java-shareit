package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookStatus;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@SpringBootTest
@AutoConfigureTestDatabase
@Sql(value = "/testdata.sql")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceImplIT {
    private final BookingServiceImpl bookingService;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    BookingDto bookingDto1, bookingDto2, bookingDto3, bookingDto4;

    @BeforeEach
    void setUp() {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(1L);
        commentDto.setText("Comment");
        commentDto.setAuthorName("user2");
        commentDto.setCreated(LocalDateTime.parse("2023-09-10T12:00:00", formatter));


        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("Item1");
        itemDto.setDescription("Item1 Description");
        itemDto.setAvailable(true);
        itemDto.setComments(Set.of(commentDto));

        UserDto userDto2 = new UserDto();
        userDto2.setId(2L);
        userDto2.setName("user2");
        userDto2.setEmail("user2@mail.ru");

        UserDto userDto3 = new UserDto();
        userDto3.setId(3L);
        userDto3.setName("user3");
        userDto3.setEmail("user3@mail.ru");

        bookingDto1 = new BookingDto();
        bookingDto1.setId(1L);
        bookingDto1.setStart(LocalDateTime.parse("2023-08-10T12:00:00", formatter));
        bookingDto1.setEnd(LocalDateTime.parse("2023-09-10T12:00:00", formatter));
        bookingDto1.setStatus(BookStatus.WAITING);
        bookingDto1.setBooker(userDto2);
        bookingDto1.setItem(itemDto);

        bookingDto2 = new BookingDto();
        bookingDto2.setId(2L);
        bookingDto2.setStart(LocalDateTime.parse("2023-04-26T12:00:00", formatter));
        bookingDto2.setEnd(LocalDateTime.parse("2023-04-27T12:00:00", formatter));
        bookingDto2.setStatus(BookStatus.REJECTED);
        bookingDto2.setBooker(userDto2);
        bookingDto2.setItem(itemDto);

        bookingDto3 = new BookingDto();
        bookingDto3.setId(3L);
        bookingDto3.setStart(LocalDateTime.parse("2023-04-26T12:00:00", formatter));
        bookingDto3.setEnd(LocalDateTime.parse("2023-04-27T12:00:00", formatter));
        bookingDto3.setStatus(BookStatus.APPROVED);
        bookingDto3.setBooker(userDto3);
        bookingDto3.setItem(itemDto);

        bookingDto4 = new BookingDto();
        bookingDto4.setId(4L);
        bookingDto4.setStart(LocalDateTime.parse("2023-05-10T12:00:00", formatter));
        bookingDto4.setEnd(LocalDateTime.parse("2023-06-10T12:00:00", formatter));
        bookingDto4.setStatus(BookStatus.APPROVED);
        bookingDto4.setBooker(userDto2);
        bookingDto4.setItem(itemDto);
    }

    @Test
    void approvingTest_changeStatusForBookingToApproved() {
        long ownerId = 1L;
        long bookingId = 1L;
        boolean isApproved = true;
        bookingDto1.setStatus(BookStatus.APPROVED);

        BookingDto bookingDtoActual = bookingService.approving(ownerId, bookingId, isApproved);

        assertEquals(bookingDto1, bookingDtoActual);
    }

    @Test
    void approvingTest_changeStatusForBookingToRejected() {
        long ownerId = 1L;
        long bookingId = 1L;
        boolean isApproved = false;
        bookingDto1.setStatus(BookStatus.REJECTED);

        BookingDto bookingDtoActual = bookingService.approving(ownerId, bookingId, isApproved);

        assertEquals(bookingDto1, bookingDtoActual);
    }

    @Test
    void getBookerStatisticsTest_returnListBookingDto() {
        int from = 0;
        int size = 20;
        long bookerId = 3L;

        String state = "PAST";
        List<BookingDto> bookingDtosActual = bookingService.getBookerStatistics(bookerId, state, from, size);
        assertEquals(List.of(bookingDto3), bookingDtosActual);

        state = "CURRENT";
        bookingDtosActual = bookingService.getBookerStatistics(bookerId, state, from, size);
        assertEquals(List.of(), bookingDtosActual);

        state = "FUTURE";
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
        assertEquals(List.of(bookingDto3), bookingDtosActual);
    }

    @Test
    void getOwnerStatisticsTest_returnListBookingDto() {
        int from = 0;
        int size = 20;
        long ownerId = 1L;

        String state = "PAST";
        List<BookingDto> bookingDtosActual = bookingService.getOwnerStatistics(ownerId, state, from, size);
        assertEquals(List.of(bookingDto3, bookingDto2), bookingDtosActual);

        state = "CURRENT";
        bookingDtosActual = bookingService.getOwnerStatistics(ownerId, state, from, size);
        assertEquals(List.of(), bookingDtosActual);

        state = "FUTURE";
        bookingDtosActual = bookingService.getOwnerStatistics(ownerId, state, from, size);
        assertEquals(List.of(bookingDto1, bookingDto4), bookingDtosActual);

        state = "WAITING";
        bookingDtosActual = bookingService.getOwnerStatistics(ownerId, state, from, size);
        assertEquals(List.of(bookingDto1), bookingDtosActual);

        state = "REJECTED";
        bookingDtosActual = bookingService.getOwnerStatistics(ownerId, state, from, size);
        assertEquals(List.of(bookingDto2), bookingDtosActual);

        state = "ALL";
        bookingDtosActual = bookingService.getOwnerStatistics(ownerId, state, from, size);
        assertEquals(List.of(bookingDto1, bookingDto4, bookingDto3, bookingDto2), bookingDtosActual);
    }
}