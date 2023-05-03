package ru.practicum.shareit.item.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Mapper(componentModel = "spring", imports = LocalDateTime.class)
public interface CommentMapper {

    Comment map(CommentDto commentDto);

    CommentDto map(Comment comment);

    List<CommentDto> map(Collection<Comment> comments);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "authorName", source = "booker.name")
    @Mapping(target = "booker", source = "booker")
    @Mapping(target = "created", expression = "java(LocalDateTime.now())")
    Comment merge(User booker, Item item, CommentDto commentDto);
}
