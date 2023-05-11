package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

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
    private ItemRequestDto itemRequestDtoIn, itemRequestDto;

    @BeforeEach
    void setUp() {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(4L);
        itemDto.setName("Item4");
        itemDto.setDescription("Item4 Description search");
        itemDto.setAvailable(true);
        itemDto.setRequestId(1L);
        itemDto.setComments(Set.of());

        itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(1L);
        itemRequestDto.setDescription("Item description");
        itemRequestDto.setCreated(created);
        itemRequestDto.setItems(List.of(itemDto));

        itemRequestDtoIn = new ItemRequestDto();
        itemRequestDtoIn.setDescription("Item description");
    }

    @Test
    void createTest_createsItemRequestAndReturnItemRequestDto() {
        long userId = 1L;
        itemRequestDto.setId(3L);
        itemRequestDto.setItems(null);

        ItemRequestDto itemRequestDtoActual = itemRequestService.create(userId, itemRequestDtoIn);
        itemRequestDtoActual.setCreated(created);

        assertEquals(itemRequestDto, itemRequestDtoActual);
    }

    @Test
    void createTest_whenUserNotFound_thenEntityNotFoundExceptionThrown() {
        long userId = 100L;

        assertThrows(EntityNotFoundException.class, () -> itemRequestService.create(userId, itemRequestDto));
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
        PageRequest page = PageRequest.of(from, size);

        Collection<ItemRequestDto> itemRequestDtosActual = itemRequestService.findAllRequestsOfOthers(userId, page);

        assertEquals(List.of(itemRequestDto), itemRequestDtosActual);
    }

}