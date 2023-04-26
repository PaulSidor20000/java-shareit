package ru.practicum.shareit.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmailContainingIgnoreCase(String email);

    @Query(
            "select b" +
                    " from User u" +
                    " join u.items i" +
                    " join i.bookings b" +
                    " join fetch b.booker" +
                    " join fetch b.item ib" +
                    " left join fetch ib.comments" +
                    " where u = :owner"
    )
    Collection<Booking> findBookingOfOwnerIdAndFetchAllEntities(@Param("owner") User owner);

    @Query(
            "select b" +
                    " from User u" +
                    " join u.bookings b" +
                    " join fetch b.booker" +
                    " join fetch b.item ib" +
                    " left join fetch ib.comments" +
                    " where u = :booker"
    )
    Collection<Booking> findBookingsOfUserAndFetchAllEntities(@Param("booker") User booker);
}
