package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Transactional
@SpringBootTest
@AutoConfigureTestDatabase
@Sql(value = "/testdata.sql")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiceImplIT {
    private final ItemServiceImpl itemService;

    ItemDto itemDto1, itemDto2;
    CommentDto commentDto;

    @BeforeEach
    void setUp() {
        itemDto1 = new ItemDto();
        itemDto1.setId(2L);
        itemDto1.setName("Item2");
        itemDto1.setDescription("Item2 Description");
        itemDto1.setAvailable(true);
        itemDto1.setComments(Set.of());

        itemDto2 = new ItemDto();
        itemDto2.setId(3L);
        itemDto2.setName("Item3 search");
        itemDto2.setDescription("Item3 Description");
        itemDto2.setAvailable(true);
        itemDto2.setComments(Set.of());

        commentDto = new CommentDto();
        commentDto.setText("First Comment");
    }

    @Test
    void findAllItemsOfOwnerTest_returnCollectionOfItemDtoOfOwner() {
        int from = 0;
        int size = 20;
        long ownerId = 2L;

        List<ItemDto> itemDtosActual = itemService.findAllItemsOfOwner(ownerId, from, size);

        assertEquals(2, itemDtosActual.size());
        assertEquals(List.of(itemDto1, itemDto2), itemDtosActual);
    }

    @Test
    void createCommentTest_createsCommentOfBookerAndReturnCommentDto() {
        long itemId = 1L;
        long bookerId = 3L;

        CommentDto commentDtoActual = itemService.createComment(itemId, bookerId, commentDto);

        assertEquals(1, commentDtoActual.getId());
        assertTrue(commentDtoActual.getCreated().isBefore(LocalDateTime.now()));
        assertEquals("First Comment", commentDtoActual.getText());
        assertEquals("user3", commentDtoActual.getAuthorName());


    }
}