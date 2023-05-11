package ru.practicum.shareit.item.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Set;

@Component
@Mapper(
        componentModel = "spring",
        imports = LocalDateTime.class
)
public interface CommentMapper {

    Comment map(CommentDto commentDto);

    CommentDto map(Comment comment);

    Set<CommentDto> mapToDtos(Set<Comment> comments);

    Set<Comment> mapToEntities(Set<CommentDto> commentDtos);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "authorName", source = "booker.name")
    @Mapping(target = "booker", source = "booker")
    @Mapping(target = "created", expression = "java(LocalDateTime.now())")
    Comment merge(User booker, Item item, CommentDto commentDto);

}
