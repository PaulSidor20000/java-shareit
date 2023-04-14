package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.BookingShort;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.List;

public interface ItemStorage extends JpaRepository<Item, Long> {

    Collection<Item> findAllByOwnerIdAndAvailableTrue(Long ownerId);

    Collection<Item> findAllByNameIsLikeIgnoreCaseOrDescriptionIsLikeIgnoreCaseAndAvailableTrue(String queryForName, String queryForDescription);

    Collection<Item> findByOwnerId(Long ownerId);

    @Query(
            "select new ru.practicum.shareit.booking.model.BookingShort(b.id, b.booker.id)" +
                    " from Booking as b " +
                    " where item = ?1" +
                    " and CURRENT_TIMESTAMP < b.start" +
                    " order by b.start"
    )
    List<BookingShort> findNextBookingsOfItem(Item item);

    @Query(
            "select new ru.practicum.shareit.booking.model.BookingShort(b.id, b.booker.id)" +
                    " from Booking as b " +
                    " where item = ?1" +
                    " and CURRENT_TIMESTAMP > b.start" +
                    " order by b.start"
    )
    List<BookingShort> findLastBookingsOfItem(Item item);

}
