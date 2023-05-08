package ru.practicum.shareit.booking.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class BookingTest {
    private final Booking expected = new Booking();
    private final Booking actual = new Booking();

    @Test
    void testBookingsEquals() {
        assertEquals(expected, actual);
    }

    @Test
    void testEqualsBookingId() {
        actual.setId(1L);
        assertNotEquals(expected, actual);
    }

    @Test
    void testEqualsBookingStatus() {
        actual.setStatus(BookStatus.WAITING);
        assertNotEquals(expected, actual);
    }

    @Test
    void testEqualsBookingStart() {
        actual.setStart(LocalDateTime.now().truncatedTo(ChronoUnit.HOURS));
        assertNotEquals(expected, actual);
    }

    @Test
    void testEqualsBookingEnd() {
        actual.setEnd(LocalDateTime.now().truncatedTo(ChronoUnit.HOURS));
        assertNotEquals(expected, actual);
    }

    @Test
    void testHashCode() {
        assertEquals(expected.hashCode(), actual.hashCode());
    }
}