package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Comment;

import java.util.Collection;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    Collection<Comment> findByItemId(Long itemId);

    @Query(
            "select c" +
                    " from Comment c" +
                    " join c.item " +
                    " where c.item.id in :items"
    )
    Collection<Comment> findByItemIds(@Param("items") Collection<Long> items);
}
