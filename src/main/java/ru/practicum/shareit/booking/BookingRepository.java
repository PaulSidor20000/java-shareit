package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.model.Booking;

import java.util.Collection;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query(
            "select b" +
                    " from Booking b" +
                    " join fetch b.item" +
                    " join fetch b.booker" +
                    " where b.id in :bookingId"
    )
    Optional<Booking> findBookingByIdAndFetchAllEntities(@Param("bookingId") Long bookingId);

    @Query(
            "select b" +
                    " from Booking b" +
                    " join fetch b.item" +
                    " join fetch b.booker" +
                    " where b.id in :bookingIds"
    )
    Collection<Booking> findBookingsAndFetchAllEntities(@Param("bookingIds") Collection<Long> bookingIds);

}
