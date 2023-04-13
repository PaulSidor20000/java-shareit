package ru.practicum.shareit.booking.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;
import org.hibernate.Hibernate;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@ToString
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "bookings")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private BookStatus status;

    @DateTimeFormat(pattern = "YYYY-MM-DDTHH:mm:ss")
    @Column(name = "start_time")
    private LocalDateTime start;

    @DateTimeFormat(pattern = "YYYY-MM-DDTHH:mm:ss")
    @Column(name = "end_time")
    private LocalDateTime end;

    @ToString.Exclude
    @JsonBackReference(value = "booker")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "booker_id", referencedColumnName = "id")
    private User booker;

    @ToString.Exclude
    @JsonBackReference(value = "item")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "item_id", referencedColumnName = "id")
    private Item item;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Booking booking = (Booking) o;
        return Objects.equals(id, booking.id)
                && status == booking.status
                && Objects.equals(start, booking.start)
                && Objects.equals(end, booking.end);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, status, start, end, booker, item);
    }
}
