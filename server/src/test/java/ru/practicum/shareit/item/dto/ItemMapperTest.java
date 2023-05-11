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
class ItemMapperTest {
    private final ItemMapper itemMapper;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;
    private ItemDto itemDto;
    private Item item;
    private User user;
    private CommentDto commentDto;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @BeforeEach
    void setUp() {
        user = userRepository.findById(1L).get();

        Comment comment = commentRepository.findById(1L).get();

        item = itemRepository.findById(1L).get();
        item.setOwner(user);
        item.setComments(Set.of(comment));

        commentDto = new CommentDto();
        commentDto.setId(1L);
        commentDto.setText("Comment");
        commentDto.setAuthorName("user2");
        commentDto.setCreated(LocalDateTime.parse("2023-09-10T12:00:00", formatter));

        itemDto = new ItemDto();
        itemDto.setName("Item1");
        itemDto.setDescription("Item1 Description");
        itemDto.setAvailable(true);
    }

    @Test
    void mergeTest_ItemDtoAndUserToItem() {
        item.setId(null);
        Item actual = itemMapper.merge(user, itemDto);

        assertEquals(item, actual);
    }

    @Test
    void mergeTest_whenOwnerAndItemDtoAreNullReturnItemNull() {
        Item actual = itemMapper.merge((User) null, null);

        assertNull(actual);
    }

    @Test
    void testMerge_mergeItemDtoToItemFromDb() {
        Item actual = itemMapper.merge(item, itemDto);

        assertEquals(item, actual);
    }

    @Test
    void mergeTest_whenItemDtoIsNullReturnItemFromDb() {
        Item actual = itemMapper.merge(item, null);

        assertEquals(item, actual);
    }

    @Test
    void mapOneForOwnerTest_mapItemToItemDtoForOwner() {
        ItemDto actual = itemMapper.mapOneForOwner(item);

        assertEquals(1, actual.getId());
        assertEquals("Item1", actual.getName());
        assertEquals("Item1 Description", actual.getDescription());
        assertTrue(actual.getAvailable());
        assertNull(actual.getRequestId());
        assertNotNull(actual.getNextBooking());
        assertNotNull(actual.getLastBooking());
        assertEquals(Set.of(commentDto), actual.getComments());
    }

    @Test
    void mapOneForOwnerTest_whenItemIsNull_thenReturnNull() {
        ItemDto actual = itemMapper.mapOneForOwner(null);

        assertNull(actual);
    }

    @Test
    void mapForUserTest_mapItemToItemDtoForUser() {
        item.setComments(null);
        ItemDto actual = itemMapper.mapForUser(item);

        assertEquals(1, actual.getId());
        assertEquals("Item1", actual.getName());
        assertEquals("Item1 Description", actual.getDescription());
        assertTrue(actual.getAvailable());
        assertNull(actual.getRequestId());
        assertNull(actual.getNextBooking());
        assertNull(actual.getLastBooking());
        assertNull(actual.getComments());
    }

    @Test
    void mapForUserTest_whenItemIsNull_thenReturnNull() {
        ItemDto actual = itemMapper.mapForUser(null);

        assertNull(actual);
    }
}