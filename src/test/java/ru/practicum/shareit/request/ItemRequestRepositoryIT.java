package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@SpringBootTest
@AutoConfigureTestDatabase
@Sql(value = "/testdata.sql")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestRepositoryIT {
    private final ItemRequestRepository itemRequestRepository;

    @Test
    void findByUserId() {
        long userId = 1L;
        ItemRequest itemRequestExpected = itemRequestRepository.findById(1L).get();

        List<ItemRequest> itemRequestsActual = itemRequestRepository.findByUserId(userId);

        assertEquals(List.of(itemRequestExpected), itemRequestsActual);
    }

    @Test
    void findAllExceptUserId() {
        long userId = 2L;
        PageRequest page = PageRequest.of(0, 20);
        ItemRequest itemRequestExpected = itemRequestRepository.findById(1L).get();

        Collection<ItemRequest> itemRequestsActual = itemRequestRepository.findAllExceptUserId(userId, page);

        assertEquals(List.of(itemRequestExpected), itemRequestsActual);
    }
}