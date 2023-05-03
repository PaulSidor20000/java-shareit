package ru.practicum.shareit.booking.dto;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.model.BookStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
//@AutoConfigureTestDatabase
//@Sql(value = "/testdata.sql")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingMapperTest {
    private final BookingMapper bookingMapper;
    private CommentDto commentDto;
    private BookingDto bookingDto;
    private Booking booking;
    private User user, owner;
    private UserDto userDto;
    private Item item;
    private ItemDto itemDto;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @BeforeEach
    void setUp() {
        Comment comment = new Comment();
        comment.setId(1L);
        comment.setText("Comment");
        comment.setAuthorName("User1");
        comment.setCreated(LocalDateTime.parse("2023-04-26T12:00:00", formatter));

        commentDto = new CommentDto();
        commentDto.setId(1L);
        commentDto.setText("Comment");
        commentDto.setAuthorName("User1");
        commentDto.setCreated(LocalDateTime.parse("2023-04-26T12:00:00", formatter));

        user = new User();
        user.setId(2L);
        user.setName("John");
        user.setEmail("john@mail.com");

        userDto = new UserDto();
        userDto.setId(2L);
        userDto.setName("John");
        userDto.setEmail("john@mail.com");


        owner = new User();
        owner.setId(1L);
        owner.setName("John");
        owner.setEmail("john@mail.com");

        item = new Item();
        item.setId(1L);
        item.setName("Item1");
        item.setDescription("Item1 Description");
        item.setAvailable(true);
        item.setOwner(owner);
        item.setComments(Set.of(comment));

        itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("Item1");
        itemDto.setDescription("Item1 Description");
        itemDto.setAvailable(true);
        itemDto.setComments(Set.of(commentDto));

        booking = new Booking();
        booking.setId(1L);
        booking.setStatus(BookStatus.WAITING);
        booking.setStart(LocalDateTime.parse("2023-04-26T12:00:00", formatter));
        booking.setEnd(LocalDateTime.parse("2023-05-26T12:00:00", formatter));
        booking.setBooker(user);
        booking.setItem(item);

        bookingDto = new BookingDto();
        bookingDto.setId(1L);
        bookingDto.setStart(LocalDateTime.parse("2023-04-26T12:00:00", formatter));
        bookingDto.setEnd(LocalDateTime.parse("2023-05-26T12:00:00", formatter));
        bookingDto.setStatus(BookStatus.WAITING);
        bookingDto.setBooker(userDto);
        bookingDto.setItem(itemDto);

    }

    @Test
    void mapTest_mapBookingDtoToBooking() {
        Booking actual = bookingMapper.map(bookingDto);

        assertEquals(booking, actual);
    }

    @Test
    void mapTest_mapBookingDtoToBooking_whenItemDtoIsNull() {
        bookingDto.setItem(null);
        booking.setItem(null);
        Booking actual = bookingMapper.map(bookingDto);

        assertEquals(booking, actual);
    }

    @Test
    void mapTest_mapBookingDtoToBooking_whenCommentsIsNull() {
        itemDto.setComments(null);
        item.setComments(null);
        Booking actual = bookingMapper.map(bookingDto);

        assertEquals(booking, actual);
    }

    @Test
    void mapTest_whenBookingDtoIsNull_thenReturnNull() {
        Booking actual = bookingMapper.map((BookingDto) null);

        assertNull(actual);
    }

    @Test
    void mapTest_mapBookingToBookingDto() {
        BookingDto actual = bookingMapper.map(booking);

        assertEquals(bookingDto, actual);
    }

    @Test
    void mapTest_mapBookingToBookingDto_whenItemDtoIsNull() {
        bookingDto.setItem(null);
        booking.setItem(null);
        BookingDto actual = bookingMapper.map(booking);

        assertEquals(bookingDto, actual);
    }

    @Test
    void mapTest_mapBookingToBookingDto_whenCommentDtoIsNull() {
        item.setComments(null);
        itemDto.setComments(null);
        BookingDto actual = bookingMapper.map(booking);

        assertEquals(bookingDto, actual);
    }

    @Test
    void mapTest_whenBookingIsNull_thenReturnNull() {
        BookingDto actual = bookingMapper.map((Booking) null);

        assertNull(actual);
    }

    @Test
    void mergeTest_mergesBookerAndItemAndBookingDtoToBooking() {
        booking.setId(null);
        Booking actual = bookingMapper.merge(user, item, bookingDto);

        assertEquals(booking, actual);
    }

    @Test
    void mergeTest_whenEntitiesAreNull_thenReturnNull() {
        Booking actual = bookingMapper.merge(null, null, null);

        assertNull(actual);
    }
}