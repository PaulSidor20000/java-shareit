package ru.practicum.shareit.item.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class ItemTest {
    private final Item expected = new Item();
    private final Item actual = new Item();

    @Test
    void testItemsEquals() {
        assertEquals(expected, actual);
    }

    @Test
    void testEqualsItemId() {
        actual.setId(1L);
        assertNotEquals(expected, actual);
    }

    @Test
    void testEqualsItemName() {
        actual.setName("");
        assertNotEquals(expected, actual);
    }

    @Test
    void testEqualsItemDescription() {
        actual.setDescription("");
        assertNotEquals(expected, actual);
    }

    @Test
    void testEqualsItemAvailable() {
        actual.setAvailable(true);
        assertNotEquals(expected, actual);
    }

    @Test
    void testHashCode() {
        assertEquals(expected.hashCode(), actual.hashCode());
    }

}