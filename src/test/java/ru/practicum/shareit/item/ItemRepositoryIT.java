package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingShort;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
@AutoConfigureTestDatabase
@Sql(value = "/testdata.sql")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRepositoryIT {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Test
    void findItemByIdAndFetchComments() {
        long itemId = 1L;

        Optional<Item> itemActual = itemRepository.findItemByIdAndFetchComments(itemId);

        assertTrue(itemActual.isPresent());
        assertEquals("Item1", itemActual.get().getName());
        assertEquals("Item1 Description", itemActual.get().getDescription());
        assertTrue(itemActual.get().isAvailable());
        assertEquals(Set.of(), itemActual.get().getComments());
    }

    @Test
    void searchByNameAndDescription() {
        Item item3 = itemRepository.findById(3L).get();
        Item item4 = itemRepository.findById(4L).get();
        PageRequest page = PageRequest.of(0, 20);
        String query = "seArch";

        List<Item> itemsActual = itemRepository.searchByNameAndDescription(query, page);

        assertEquals(2, itemsActual.size());
        assertEquals(item3, itemsActual.get(0));
        assertEquals(Set.of(), itemsActual.get(0).getComments());
        assertEquals(item4, itemsActual.get(1));
        assertEquals(Set.of(), itemsActual.get(1).getComments());
    }

    @Test
    void findItemsByOwnerIdAndFetchAllEntities() {
        long ownerId = 2L;
        Item item2 = itemRepository.findById(2L).get();
        Item item3 = itemRepository.findById(3L).get();
        PageRequest page = PageRequest.of(0, 20);

        List<Item> itemsActual = itemRepository.findItemsByOwnerIdAndFetchAllEntities(ownerId, page);

        assertEquals(2, itemsActual.size());
        assertEquals(item2, itemsActual.get(0));
        assertEquals(Set.of(), itemsActual.get(0).getComments());
        assertEquals(item3, itemsActual.get(1));
        assertEquals(Set.of(), itemsActual.get(1).getComments());
    }

    @Test
    void findNextBookings() {
        Collection<Long> itemIds = List.of(1L);

        List<BookingShort> bookingShorts = itemRepository.findNextBookings(itemIds);

        assertFalse(bookingShorts.isEmpty());
        assertEquals(1, bookingShorts.size());
    }

    @Test
    void findLastBookings() {
        Collection<Long> itemIds = List.of(1L);

        List<BookingShort> bookingShorts = itemRepository.findLastBookings(itemIds);

        assertFalse(bookingShorts.isEmpty());
        assertEquals(1, bookingShorts.size());
    }

    @Test
    void findNextBookingsOfItem() {
        long itemId = 1L;

        Optional<BookingShort> bookingShort = itemRepository.findNextBookingsOfItem(itemId);

        assertTrue(bookingShort.isPresent());
    }

    @Test
    void findLastBookingsOfItem() {
        long itemId = 1L;

        Optional<BookingShort> bookingShort = itemRepository.findLastBookingsOfItem(itemId);

        assertTrue(bookingShort.isPresent());
    }

    @Test
    void findAllItemsByRequestIds() {
        Item item4 = itemRepository.findById(4L).get();
        Set<Long> requestIds = Set.of(1L);

        List<Item> itemsActual = itemRepository.findAllItemsByRequestIds(requestIds);

        assertEquals(1, itemsActual.size());
        assertEquals(item4, itemsActual.get(0));
        assertEquals(Set.of(), itemsActual.get(0).getComments());
    }
}