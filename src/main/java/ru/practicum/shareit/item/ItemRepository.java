package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.BookingShort;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemRepository extends JpaRepository<Item, Long> {

    Collection<Item> findAllByOwnerIdAndAvailableTrue(Long ownerId);

    Collection<Item> findAllByNameIsLikeIgnoreCaseOrDescriptionIsLikeIgnoreCaseAndAvailableTrue(String queryForName, String queryForDescription);

    @Query(
            "select new ru.practicum.shareit.booking.model.BookingShort(b.id, b.booker.id, b.item.id)" +
                    " from Booking as b " +
                    " where item in ?1" +
                    " and CURRENT_TIMESTAMP < b.start" +
                    " and b.status = 'APPROVED'" +
                    " order by b.start"
    )
    Collection<BookingShort> findNextBookings(Collection<Item> items);

    @Query(
            "select new ru.practicum.shareit.booking.model.BookingShort(b.id, b.booker.id, b.item.id)" +
                    " from Booking as b " +
                    " where item in ?1" +
                    " and CURRENT_TIMESTAMP > b.start" +
                    " and b.status = 'APPROVED'" +
                    " order by b.start desc"
    )
    Collection<BookingShort> findLastBookings(Collection<Item> items);

    @Query(
            "select new ru.practicum.shareit.booking.model.BookingShort(b.id, b.booker.id, b.item.id)" +
                    " from Booking as b " +
                    " where item = ?1" +
                    " and CURRENT_TIMESTAMP < b.start" +
                    " and b.status = 'APPROVED'" +
                    " order by b.start"
    )
    Collection<BookingShort> findNextBookingsOfItem(Item item);

    @Query(
            "select new ru.practicum.shareit.booking.model.BookingShort(b.id, b.booker.id, b.item.id)" +
                    " from Booking as b " +
                    " where item = ?1" +
                    " and CURRENT_TIMESTAMP > b.start" +
                    " and b.status = 'APPROVED'" +
                    " order by b.start desc"
    )
    Collection<BookingShort> findLastBookingsOfItem(Item item);

}
