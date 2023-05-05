package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ItemRequestDtoTest {
    private final ItemRequestDto expected = new ItemRequestDto();
    private final ItemRequestDto actual = new ItemRequestDto();

    @Test
    void testEquals() {
        assertEquals(expected, actual);
    }

    @Test
    void testHashCode() {
        assertEquals(expected.hashCode(), actual.hashCode());
    }

}