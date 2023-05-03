package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest
@AutoConfigureTestDatabase
@Sql(value = "/testdata.sql")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestServiceImplIT {
    private final ItemRequestServiceImpl itemRequestService;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    private final LocalDateTime created = LocalDateTime.parse("2023-08-10T12:00:00", formatter);
    private ItemRequestDto itemRequestDtoIn, itemRequestDtoOut, itemRequestDto;
    private User user1, owner3;
    private Item item4;
    private ItemDto itemDto4;
    private ItemRequest itemRequest;

    @BeforeEach
    void setUp() {
        user1 = new User();
        user1.setId(1L);
        user1.setName("John");
        user1.setEmail("john@mail.com");

        owner3 = new User();
        owner3.setId(3L);
        owner3.setName("User3");
        owner3.setEmail("user3@mail.ru");

        item4 = new Item();
        item4.setId(4L);
        item4.setName("Item4");
        item4.setDescription("Item4 Description search");
        item4.setAvailable(true);
        item4.setRequestId(1L);
        item4.setOwner(owner3);

        itemDto4 = new ItemDto();
        itemDto4.setId(4L);
        itemDto4.setName("Item4");
        itemDto4.setDescription("Item4 Description search");
        itemDto4.setAvailable(true);
        itemDto4.setRequestId(1L);
        itemDto4.setComments(Set.of());

        itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setDescription("Item description");
        itemRequest.setCreated(created);
        itemRequest.setUser(user1);

        itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(1L);
        itemRequestDto.setDescription("Item description");
        itemRequestDto.setCreated(created);
        itemRequestDto.setItems(List.of(itemDto4));

        itemRequestDtoIn = new ItemRequestDto();
        itemRequestDtoIn.setDescription("Item description");
        itemRequestDtoIn.setCreated(created);

        itemRequestDtoOut = new ItemRequestDto();
        itemRequestDtoOut.setId(3L);
        itemRequestDtoOut.setDescription("Item description");
        itemRequestDtoOut.setCreated(created);
    }

    @Test
    void createTest_createsItemRequestAndReturnItemRequestDto() {
        long userId = 1L;

        ItemRequestDto itemRequestDtoActual = itemRequestService.create(userId, itemRequestDtoIn);
        itemRequestDtoActual.setCreated(created);

        assertEquals(itemRequestDtoOut, itemRequestDtoActual);
    }

    @Test
    void createTest_whenUserNotFound_thenEntityNotFoundExceptionThrown() {
        long userId = 100L;

        assertThrows(EntityNotFoundException.class, () -> itemRequestService.create(userId, itemRequestDtoIn));
    }

    @Test
    void readTest_returnItemRequestDto() {
        long userId = 1L;
        long requestId = 1L;

        ItemRequestDto itemRequestDtoActual = itemRequestService.read(requestId, userId);

        assertEquals(itemRequestDto, itemRequestDtoActual);
    }

    @Test
    void findAllRequestsOfUserTest_returnCollectionItemRequestDto() {
        long userId = 1L;

        Collection<ItemRequestDto> itemRequestDtosActual = itemRequestService.findAllRequestsOfUser(userId);

        assertEquals(List.of(itemRequestDto), itemRequestDtosActual);
    }

    @Test
    void findAllRequestsOfOthersTest_returnCollectionItemRequestDto() {
        int from = 0;
        int size = 20;
        long userId = 2L;

        Collection<ItemRequestDto> itemRequestDtosActual = itemRequestService.findAllRequestsOfOthers(userId, from, size);

        assertEquals(List.of(itemRequestDto), itemRequestDtosActual);
    }

}