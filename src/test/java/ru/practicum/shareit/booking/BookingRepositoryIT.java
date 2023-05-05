package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Transactional
@SpringBootTest
@AutoConfigureTestDatabase
@Sql(value = "/testdata.sql")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingRepositoryIT {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Test
    void findBookingByIdAndFetchAllEntities() {
        Long itemId = 1L;
        Long bookerId = 2L;
        Long bookingId = 1L;
        Item item = itemRepository.findById(itemId).get();
        User booker = userRepository.findById(bookerId).get();
        Booking bookingExpected = bookingRepository.findById(bookingId).get();
        bookingExpected.setBooker(booker);
        bookingExpected.setItem(item);

        Optional<Booking> bookingActual = bookingRepository.findBookingByIdAndFetchAllEntities(bookingId);

        assertTrue(bookingActual.isPresent());
        assertEquals(bookingExpected, bookingActual.get());

    }
}