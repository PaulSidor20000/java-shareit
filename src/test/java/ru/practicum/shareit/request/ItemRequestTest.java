package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ItemRequestTest {
    private final ItemRequest expected = new ItemRequest();
    private final ItemRequest actual = new ItemRequest();

    @Test
    void testEquals() {
        assertEquals(expected, actual);
    }

    @Test
    void testHashCode() {
        assertEquals(expected.hashCode(), actual.hashCode());
    }
}