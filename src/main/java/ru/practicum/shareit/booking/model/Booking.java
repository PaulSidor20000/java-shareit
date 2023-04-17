package ru.practicum.shareit.booking.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "bookings")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
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
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "booker_id", referencedColumnName = "id")
    private User booker;

    @ToString.Exclude
    @JsonBackReference(value = "item")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
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
        return Objects.hash(id, status, start, end);
    }
}
