package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BookingDtoTest {
    private final BookingDto expected = new BookingDto();
    private final BookingDto actual = new BookingDto();

    @Test
    void testEquals() {
        assertEquals(expected, actual);
    }

    @Test
    void testHashCode() {
        assertEquals(expected.hashCode(), actual.hashCode());
    }

}