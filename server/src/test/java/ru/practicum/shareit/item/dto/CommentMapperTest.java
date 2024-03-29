package ru.practicum.shareit.item.dto;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.item.CommentRepository;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@Sql(value = "/testdata.sql")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class CommentMapperTest {
    private final CommentMapper commentMapper;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    private CommentDto commentDto;
    private Comment comment;
    private User booker;
    private Item item;

    @BeforeEach
    void setUp() {
        comment = commentRepository.findById(1L).orElse(null);

        User owner = userRepository.findById(1L).orElse(null);

        booker = userRepository.findById(2L).orElse(null);

        item = itemRepository.findById(1L).orElse(null);
        assert item != null;
        item.setOwner(owner);

        commentDto = new CommentDto();
        commentDto.setText("Comment");
    }

    @Test
    void mapTest_mapCommentDtoToComment() {
        Comment actual = commentMapper.map(commentDto);

        assertNull(actual.getId());
        assertEquals("Comment", actual.getText());
        assertNull(actual.getAuthorName());
        assertNull(actual.getCreated());
        assertNull(actual.getItem());
        assertNull(actual.getBooker());
    }

    @Test
    void mapTest_whenCommentDtoIsNullReturnCommentNull() {
        Comment actual = commentMapper.map((CommentDto) null);

        assertNull(actual);
    }

    @Test
    void testMap_mapCommentToCommentDto() {
        CommentDto actual = commentMapper.map(comment);

        assertEquals(1, actual.getId());
        assertEquals("Comment", actual.getText());
        assertEquals("user2", actual.getAuthorName());
        assertEquals(LocalDateTime.parse("2023-09-10T12:00:00", formatter), actual.getCreated());
    }

    @Test
    void mapTest_whenCommentIsNullReturnCommentDtoNull() {
        CommentDto actual = commentMapper.map((Comment) null);

        assertNull(actual);
    }

    @Test
    void mapTest_mapCollectionOfCommentsToCollectionOfCommentDto() {
        Set<CommentDto> actual = commentMapper.mapToDtos(Set.of(comment));
        commentDto.setId(1L);
        commentDto.setAuthorName("user2");
        commentDto.setCreated(LocalDateTime.parse("2023-09-10T12:00:00", formatter));

        assertTrue(actual.contains(commentDto));
    }

    @Test
    void mapTest_whenCollectionIsNullReturnCollectionNull() {
        Set<CommentDto> actual = commentMapper.mapToDtos((Set<Comment>) null);

        assertNull(actual);
    }

    @Test
    void mergeTest_mergeCommentDtoAndItemAndBookerToComment() {
        Comment actual = commentMapper.merge(booker, item, commentDto);

        assertNull(actual.getId());
        assertEquals("Comment", actual.getText());
        assertEquals("user2", actual.getAuthorName());
        assertNotNull(actual.getCreated());
        assertEquals(item, actual.getItem());
        assertEquals(booker, actual.getBooker());
    }

    @Test
    void mergeTest_whenBookerAndItemAndCommentDtoAreNullReturnCommentNull() {
        Comment actual = commentMapper.merge(null, null, null);

        assertNull(actual);
    }

}