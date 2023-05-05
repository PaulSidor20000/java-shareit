package ru.practicum.shareit.request.dto;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@Sql(value = "/testdata.sql")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestMapperTest {
    private final ItemRequestMapper itemRequestMapper;
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    private ItemRequest itemRequest;
    private ItemRequestDto itemRequestDto;
    private User booker;
    private Item item;
    private ItemDto itemDto;

    @BeforeEach
    void setUp() {
        itemRequest = itemRequestRepository.findById(1L).get();

        itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("Item description");

        User owner = userRepository.findById(3L).get();

        booker = userRepository.findById(2L).get();

        item = itemRepository.findById(4L).get();
        item.setOwner(owner);
        item.setComments(Set.of());

        itemDto = new ItemDto();
        itemDto.setId(4L);
        itemDto.setName("Item4");
        itemDto.setDescription("Item4 Description search");
        itemDto.setAvailable(true);
        itemDto.setRequestId(1L);
        itemDto.setComments(Set.of());
    }

    @Test
    void mapTest_mapsItemRequestToItemRequestDto() {
        ItemRequestDto actual = itemRequestMapper.map(itemRequest);

        assertEquals(1, actual.getId());
        assertEquals("Item description", actual.getDescription());
        assertEquals(LocalDateTime.parse("2023-08-10T12:00:00", formatter), actual.getCreated());
        assertNull(actual.getItems());
    }

    @Test
    void mapTest_whenItemRequestIsNull_returnsItemRequestDtoNull() {
        ItemRequestDto actual = itemRequestMapper.map(null);

        assertNull(actual);
    }

    @Test
    void testMerge_mergesItemRequestDtoAndUserToItemRequest() {
        ItemRequest actual = itemRequestMapper.merge(booker, itemRequestDto);

        assertNull(actual.getId());
        assertEquals("Item description", actual.getDescription());
        assertNotNull(actual.getCreated());
        assertEquals(booker, actual.getUser());
    }

    @Test
    void mergeTest_whenItemRequestDtoAndUserAreNull_returnsItemRequestNull() {
        ItemRequest actual = itemRequestMapper.merge((User) null, null);

        assertNull(actual);
    }

    @Test
    void mergeTest_mergesItemRequestAndItemsToItemRequestDto() {
        ItemRequestDto actual = itemRequestMapper.merge(List.of(item), itemRequest);

        assertEquals(1, actual.getId());
        assertEquals("Item description", actual.getDescription());
        assertEquals(LocalDateTime.parse("2023-08-10T12:00:00", formatter), actual.getCreated());
        assertEquals(List.of(itemDto), actual.getItems());
    }

    @Test
    void mergeTest_whenItemRequestAndListOfItemsAreNull_returnsItemRequestDtoNull() {
        ItemRequestDto actual = itemRequestMapper.merge((List<Item>) null, null);

        assertNull(actual);
    }

    @Test
    void mergeTest_whenListOfItemsAreNull_returnsItemRequestDto() {
        ItemRequestDto actual = itemRequestMapper.merge(null, itemRequest);

        assertEquals(1, actual.getId());
        assertEquals("Item description", actual.getDescription());
        assertEquals(LocalDateTime.parse("2023-08-10T12:00:00", formatter), actual.getCreated());
        assertNull(actual.getItems());
    }

}