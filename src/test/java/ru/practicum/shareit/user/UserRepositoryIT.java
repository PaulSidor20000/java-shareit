package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Sql(value = "/testdata.sql")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserRepositoryIT {
    private final UserRepository userRepository;

    @Test
    void findByEmailContainingIgnoreCase() {
        Optional<User> actualUser = userRepository.findByEmailContainingIgnoreCase("user1@mail.ru");

        assertTrue(actualUser.isPresent());
        assertEquals("user1@mail.ru", actualUser.get().getEmail());
    }

    @Test
    void findBookingOfOwnerIdAndFetchAllEntities() {
        User owner = userRepository.findById(1L).get();
        PageRequest page = PageRequest.of(0, 20);

        List<Booking> bookings = userRepository.findBookingOfOwnerIdAndFetchAllEntities(owner, page);

        assertFalse(bookings.isEmpty());
        assertEquals(4, bookings.size());
        assertEquals(owner, bookings.get(0).getItem().getOwner());
    }

    @Test
    void findBookingsOfUserAndFetchAllEntities() {
        User booker = userRepository.findById(2L).get();
        PageRequest page = PageRequest.of(0, 20);

        List<Booking> bookings = userRepository.findBookingsOfUserAndFetchAllEntities(booker, page);

        assertFalse(bookings.isEmpty());
        assertEquals(3, bookings.size());
        assertEquals(booker, bookings.get(0).getBooker());
    }
}