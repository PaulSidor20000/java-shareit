package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query(
            "select new ru.practicum.shareit.item.dto.CommentDto(c.id, c.text, c.authorName, c.created)" +
                    " from Comment as c" +
                    " where c.id = ?1"
    )
    CommentDto findDto(Long id);
}
