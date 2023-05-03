package ru.practicum.shareit.item.dto;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
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
    private User owner;
    private ItemDto itemDtoIn;
    private Item item, itemFromDB;
    private CommentDto commentDto;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @BeforeEach
    void setUp() {
        Comment comment = new Comment();
        comment.setId(1L);
        comment.setText("Comment");
        comment.setAuthorName("User1");
        comment.setCreated(LocalDateTime.parse("2023-04-26T12:00:00", formatter));

        commentDto = new CommentDto();
        commentDto.setId(1L);
        commentDto.setText("Comment");
        commentDto.setAuthorName("User1");
        commentDto.setCreated(LocalDateTime.parse("2023-04-26T12:00:00", formatter));

        owner = new User();
        owner.setId(1L);
        owner.setName("John");
        owner.setEmail("john@mail.com");

        item = new Item();
        item.setId(1L);
        item.setName("Item1");
        item.setDescription("Item1 Description");
        item.setAvailable(true);
        item.setOwner(owner);
        item.setComments(Set.of(comment));

        itemFromDB = new Item();
        itemFromDB.setId(1L);
        itemFromDB.setName("Item");
        itemFromDB.setDescription("Item");
        itemFromDB.setAvailable(false);
        itemFromDB.setOwner(owner);

        itemDtoIn = new ItemDto();
        itemDtoIn.setName("Item1");
        itemDtoIn.setDescription("Item1 Description");
        itemDtoIn.setAvailable(true);
    }

    @Test
    void mergeTest_ItemDtoAndUserToItem() {
        item.setId(null);
        Item actual = itemMapper.merge(owner, itemDtoIn);

        assertEquals(item, actual);
    }

    @Test
    void mergeTest_whenOwnerAndItemDtoAreNullReturnItemNull() {
        Item actual = itemMapper.merge((User) null, null);

        assertNull(actual);
    }

    @Test
    void testMerge_mergeItemDtoToItemFromDb() {
        Item actual = itemMapper.merge(itemFromDB, itemDtoIn);

        assertEquals(item, actual);
    }

    @Test
    void mergeTest_whenItemDtoIsNullReturnItemFromDb() {
        Item actual = itemMapper.merge(itemFromDB, null);

        assertEquals(itemFromDB, actual);
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