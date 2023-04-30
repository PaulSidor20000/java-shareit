package ru.practicum.shareit;

import org.junit.jupiter.api.BeforeEach;
import ru.practicum.shareit.booking.dto.BookingShort;
import ru.practicum.shareit.booking.dto.BookingShortImpl;
import ru.practicum.shareit.booking.model.BookStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Set;

public class TestEnvironment {
    protected UserDto userDtoIn, userDtoOut, userDtoPatchName, userDtoPatchEmail;
    protected User user, owner, userWithBookings;
    protected ItemDto itemDtoIn, itemDtoOut, itemDtoPatch;
    protected Item item, item2;
    protected BookingShort nextBooking, lastBooking;
    protected Booking booking, bookingRejected, booking2;
    protected CommentDto commentDto;
    protected Comment comment;

    @BeforeEach
    void setUp() {
        userDtoPatchName = new UserDto();
        userDtoPatchName.setName("Sam");

        userDtoPatchEmail = new UserDto();
        userDtoPatchEmail.setEmail("sam@mail.com");

        userDtoIn = new UserDto();
        userDtoIn.setName("John");
        userDtoIn.setEmail("john@mail.com");

        userDtoOut = new UserDto();
        userDtoOut.setId(1L);
        userDtoOut.setName("John");
        userDtoOut.setEmail("john@mail.com");

        user = new User();
        user.setId(1L);
        user.setName("John");
        user.setEmail("john@mail.com");

        userWithBookings = new User();
        userWithBookings.setId(3L);
        userWithBookings.setName("Mark");
        userWithBookings.setEmail("mark@mail.com");

        owner = new User();
        owner.setId(2L);
        owner.setName("Owner");
        owner.setEmail("owner@mail.ru");


        itemDtoIn = new ItemDto();
        itemDtoIn.setName("Item1");
        itemDtoIn.setDescription("Item1 Description");
        itemDtoIn.setAvailable(true);

        itemDtoOut = new ItemDto();
        itemDtoOut.setId(1L);
        itemDtoOut.setName("Item1");
        itemDtoOut.setDescription("Item1 Description");
        itemDtoOut.setAvailable(true);

        itemDtoPatch = new ItemDto();


        item = new Item();
        item.setId(1L);
        item.setName("Item1");
        item.setDescription("Item1 Description");
        item.setAvailable(true);
        item.setOwner(owner);

        item2 = new Item();
        item2.setId(2L);
        item2.setName("Item2");
        item2.setDescription("Item2 Description");
        item2.setAvailable(true);
        item2.setOwner(owner);

        owner.setItems(Set.of(item, item2));

        nextBooking = new BookingShortImpl(1L, 1L, 1L);
        lastBooking = new BookingShortImpl(1L, 1L, 1L);

        booking = new Booking();
        booking.setId(1L);
        booking.setStatus(BookStatus.APPROVED);
        booking.setStart(LocalDateTime.now().minusDays(2));
        booking.setEnd(LocalDateTime.now().minusDays(1));
        booking.setItem(item);

        booking2 = new Booking();
        booking2.setId(3L);
        booking2.setStatus(BookStatus.APPROVED);
        booking2.setStart(LocalDateTime.now().plusDays(1));
        booking2.setEnd(LocalDateTime.now().plusDays(2));
        booking2.setItem(item);

        bookingRejected = new Booking();
        bookingRejected.setId(2L);
        bookingRejected.setStatus(BookStatus.REJECTED);
        bookingRejected.setStart(LocalDateTime.now().minusDays(1));
        bookingRejected.setEnd(LocalDateTime.now());
        bookingRejected.setItem(item2);

        item.setBookings(Set.of(booking, booking2));

        item2.setBookings(Set.of(bookingRejected));

        userWithBookings.setBookings(Set.of(booking, bookingRejected));

        commentDto = new CommentDto();
        commentDto.setText("First Comment");

        comment = new Comment();


    }
}
