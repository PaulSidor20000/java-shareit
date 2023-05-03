package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.model.BookStatus;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingDtoJsonTest {
    @Autowired
    private JacksonTester<BookingDto> json;
    BookingDto bookingDto;
    CommentDto commentDto;
    ItemDto itemDto;
    UserDto userDto;

    @BeforeEach
    void setUp() {
        userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("User1");
        userDto.setEmail("user1@mail.ru");

        commentDto = new CommentDto();
        commentDto.setId(1L);
        commentDto.setText("Comment");
        commentDto.setAuthorName("User1");
        commentDto.setCreated(LocalDateTime.now());
        //  commentDto.setItemId(1L);

        itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("Name");
        itemDto.setDescription("Description");
        itemDto.setAvailable(true);
        itemDto.setRequestId(null);
        itemDto.setNextBooking(new BookingShortImpl(1L, 1L, 1L));
        itemDto.setLastBooking(new BookingShortImpl(2L, 2L, 2L));
        itemDto.setComments(Set.of(commentDto));

        bookingDto = new BookingDto();
        bookingDto.setId(1L);
        bookingDto.setItemId(null);
        bookingDto.setStart(LocalDateTime.now().plusDays(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(2));
        bookingDto.setStatus(BookStatus.APPROVED);
        bookingDto.setBooker(userDto);
        bookingDto.setItem(itemDto);
    }

    @Test
    void testItemBookingDto() throws Exception {
        JsonContent<BookingDto> result = json.write(bookingDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isNull();
        assertThat(result).extractingJsonPathValue("$.start").isNotNull();
        assertThat(result).extractingJsonPathValue("$.end").isNotNull();
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("APPROVED");

        assertThat(result).extractingJsonPathMapValue("$.booker").hasSize(3);
        assertThat(result).extractingJsonPathNumberValue("$.booker.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.booker.name").isEqualTo("User1");
        assertThat(result).extractingJsonPathStringValue("$.booker.email").isEqualTo("user1@mail.ru");

        assertThat(result).extractingJsonPathMapValue("$.item").hasSize(8);
        assertThat(result).extractingJsonPathNumberValue("$.item.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.item.name").isEqualTo("Name");
        assertThat(result).extractingJsonPathStringValue("$.item.description").isEqualTo("Description");
        assertThat(result).extractingJsonPathBooleanValue("$.item.available").isEqualTo(true);
        assertThat(result).extractingJsonPathNumberValue("$.item.requestId").isNull();

        assertThat(result).extractingJsonPathMapValue("$.item.nextBooking").hasSize(3);
        assertThat(result).extractingJsonPathNumberValue("$.item.nextBooking.id").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.item.nextBooking.bookerId").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.item.nextBooking.itemId").isEqualTo(1);

        assertThat(result).extractingJsonPathMapValue("$.item.lastBooking").hasSize(3);
        assertThat(result).extractingJsonPathNumberValue("$.item.lastBooking.id").isEqualTo(2);
        assertThat(result).extractingJsonPathNumberValue("$.item.lastBooking.bookerId").isEqualTo(2);
        assertThat(result).extractingJsonPathNumberValue("$.item.lastBooking.itemId").isEqualTo(2);

        assertThat(result).extractingJsonPathArrayValue("$.item.comments").hasSize(1);
        assertThat(result).extractingJsonPathNumberValue("$.item.comments[0].id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.item.comments[0].text").isEqualTo("Comment");
        assertThat(result).extractingJsonPathStringValue("$.item.comments[0].authorName").isEqualTo("User1");
        assertThat(result).extractingJsonPathValue("$.item.comments[0].created").isNotNull();
//        assertThat(result).extractingJsonPathNumberValue("$.item.comments[0].itemId").isEqualTo(1);
    }
}