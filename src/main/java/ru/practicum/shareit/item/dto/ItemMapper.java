package ru.practicum.shareit.item.dto;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

@Mapper
public interface ItemMapper {
    ItemMapper mapper = Mappers.getMapper(ItemMapper.class);

    Item map(ItemDto itemDto);

    ItemDto map(Item item);

    Comment map(CommentDto commentDto);

    CommentDto map(Comment comment);

    Collection<CommentDto> map(Collection<Comment> comments);

}
