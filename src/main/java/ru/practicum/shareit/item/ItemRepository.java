package ru.practicum.shareit.item;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.dto.BookingShort;
import ru.practicum.shareit.item.model.Item;

import java.awt.print.Pageable;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query(
            "select i" +
                    " from Item i" +
                    " left join fetch i.comments" +
                    " where i.id = :id"
    )
    Optional<Item> findItemByIdAndFetchComments(@Param("id") Long itemId);

    @Query(
            "select i" +
                    " from Item i" +
                    " left join fetch i.comments" +
                    " where i.available = true" +
                    " and (lower(i.name) like lower(concat('%', :query, '%'))" +
                    " or lower(i.description) like lower(concat('%', :query, '%')))" +
                    " order by i.id"
    )
    Collection<Item> searchByNameAndDescription(@Param("query") String query, PageRequest page);

    @Query(
            "select i" +
                    " from Item i" +
                    " join i.owner o" +
                    " left join fetch i.comments" +
                    " where o.id = :id"
    )
    Collection<Item> findItemsByOwnerIdAndFetchAllEntities(@Param("id") Long ownerId, PageRequest page);

    @Query(
            value = "select b.id as id, b.booker_id as bookerId, b.item_id as itemId" +
                    " from bookings as b " +
                    " left join items i on i.id = b.item_id" +
                    " where b.item_id in (:itemIds)" +
                    " and CURRENT_TIMESTAMP < b.start_time" +
                    " and b.status = 'APPROVED'" +
                    " order by b.start_time" +
                    " limit 1",
            nativeQuery = true
    )
    Collection<BookingShort> findNextBookings(@Param("itemIds") Collection<Long> itemIds);

    @Query(
            value = "select b.id as id, b.booker_id as bookerId, b.item_id as itemId" +
                    " from bookings as b " +
                    " left join items i on i.id = b.item_id" +
                    " where b.item_id in (:itemIds)" +
                    " and CURRENT_TIMESTAMP > b.start_time" +
                    " and b.status = 'APPROVED'" +
                    " order by b.start_time desc" +
                    " limit 1",
            nativeQuery = true
    )
    Collection<BookingShort> findLastBookings(@Param("itemIds") Collection<Long> itemIds);

    @Query(
            value = "select b.id as id, b.booker_id as bookerId, b.item_id as itemId" +
                    " from bookings as b" +
                    " left join items i on i.id = b.item_id" +
                    " where b.item_id in (:itemId)" +
                    " and CURRENT_TIMESTAMP < b.start_time" +
                    " and b.status = 'APPROVED'" +
                    " order by b.start_time" +
                    " limit 1",
            nativeQuery = true
    )
    Optional<BookingShort> findNextBookingsOfItem(@Param("itemId") Long itemId);

    @Query(
            value = "select b.id as id, b.booker_id as bookerId, b.item_id as itemId" +
                    " from bookings as b" +
                    " left join items i on i.id = b.item_id" +
                    " where b.item_id in (:itemId)" +
                    " and CURRENT_TIMESTAMP > b.start_time" +
                    " and b.status = 'APPROVED'" +
                    " order by b.start_time desc" +
                    " limit 1",
            nativeQuery = true
    )
    Optional<BookingShort> findLastBookingsOfItem(@Param("itemId") Long itemId);

    @Query(
            "select i" +
                    " from Item i" +
                    " left join fetch i.comments" +
                    " where i.requestId in :ids"
    )
    List<Item> findAllItemsByRequestIds(@Param("ids") Set<Long> requestIds);
}
