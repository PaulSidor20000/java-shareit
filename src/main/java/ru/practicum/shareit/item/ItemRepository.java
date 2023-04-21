package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.model.BookingShort;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemRepository extends JpaRepository<Item, Long> {

    Collection<Item> findAllByOwnerIdAndAvailableTrue(Long ownerId);

    Collection<Item> findAllByNameIsLikeIgnoreCaseOrDescriptionIsLikeIgnoreCaseAndAvailableTrue(String queryForName, String queryForDescription);

    @Query(
            "select new ru.practicum.shareit.booking.model.BookingShort(b.id, b.booker.id, b.item.id)" +
                    " from Booking as b " +
                    " where b.item in :items" +
                    " and CURRENT_TIMESTAMP < b.start" +
                    " and b.status = 'APPROVED'" +
                    " order by b.start"
    )
    Collection<BookingShort> findNextBookings(@Param("items") Collection<Item> items);

    @Query(
            "select new ru.practicum.shareit.booking.model.BookingShort(b.id, b.booker.id, b.item.id)" +
                    " from Booking as b " +
                    " where b.item in :items" +
                    " and CURRENT_TIMESTAMP > b.start" +
                    " and b.status = 'APPROVED'" +
                    " order by b.start desc"
    )
    Collection<BookingShort> findLastBookings(@Param("items") Collection<Item> items);

    @Query(
            "select new ru.practicum.shareit.booking.model.BookingShort(b.id, b.booker.id, b.item.id)" +
                    " from Booking as b " +
                    " where b.item = :items" +
                    " and CURRENT_TIMESTAMP < b.start" +
                    " and b.status = 'APPROVED'" +
                    " order by b.start"
    )
    Collection<BookingShort> findNextBookingsOfItem(@Param("items") Item item);

    @Query(
            "select new ru.practicum.shareit.booking.model.BookingShort(b.id, b.booker.id, b.item.id)" +
                    " from Booking as b " +
                    " where b.item = :items" +
                    " and CURRENT_TIMESTAMP > b.start" +
                    " and b.status = 'APPROVED'" +
                    " order by b.start desc"
    )
    Collection<BookingShort> findLastBookingsOfItem(@Param("items") Item item);

}
