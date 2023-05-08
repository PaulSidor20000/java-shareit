package ru.practicum.shareit.user.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {
    private final User expected = new User();
    private final User actual = new User();

    @Test
    void testUsersEquals() {
        assertEquals(expected, actual);
    }

    @Test
    void testEqualsUserId() {
        actual.setId(1L);
        assertNotEquals(expected, actual);
    }

    @Test
    void testEqualsUserName() {
        actual.setName("");
        assertNotEquals(expected, actual);
    }

    @Test
    void testEqualsUserEmail() {
        actual.setEmail("");
        assertNotEquals(expected, actual);
    }

    @Test
    void testHashCode() {
        assertEquals(expected.hashCode(), actual.hashCode());
    }
}