package ru.practicum.shareit.item.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class CommentTest {
    private final Comment expected = new Comment();
    private final Comment actual = new Comment();

    @Test
    void testCommentEquals() {
        assertEquals(expected, actual);
    }

    @Test
    void testEqualsCommentId() {
        actual.setId(1L);
        assertNotEquals(expected, actual);
    }

    @Test
    void testEqualsCommentText() {
        actual.setText("");
        assertNotEquals(expected, actual);
    }

    @Test
    void testEqualsCommentAuthorName() {
        actual.setAuthorName("");
        assertNotEquals(expected, actual);
    }

    @Test
    void testEqualsCommentCreated() {
        actual.setCreated(LocalDateTime.now().truncatedTo(ChronoUnit.HOURS));
        assertNotEquals(expected, actual);
    }

    @Test
    void testHashCode() {
        assertEquals(expected.hashCode(), actual.hashCode());
    }

}