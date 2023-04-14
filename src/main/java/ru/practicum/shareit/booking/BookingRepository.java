package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingShort;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    public Collection<Booking> findByItem(Item item);

//    @Query(
//            "select new ru.practicum.shareit.booking.model.BookingShort(b.id, b.booker.id)" +
//                    " from Booking as b " +
//                    " where item = ?1"
//    )
//    Collection<BookingShort> findBookingIdAndBookerIdByItem(Item item);


}
