package ru.practicum.shareit.item.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
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
@Table(name = "comments")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "text", nullable = false)
    private String text;

    @Column(name = "author_name")
    private String authorName;

    @DateTimeFormat(pattern = "YYYY-MM-DDTHH:mm:ss")
    private LocalDateTime created;

    @ToString.Exclude
    @JsonBackReference(value = "comment")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", referencedColumnName = "id")
    private Item item;

    @ToString.Exclude
    @JsonBackReference(value = "booker_comment")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booker_id", referencedColumnName = "id")
    private User booker;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Comment comment = (Comment) o;
        return Objects.equals(id, comment.id)
                && Objects.equals(text, comment.text)
                && Objects.equals(authorName, comment.authorName)
                && Objects.equals(created, comment.created);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, text, authorName, created);
    }
}
