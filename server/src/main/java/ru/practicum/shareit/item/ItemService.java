package ru.practicum.shareit.item;

import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

public interface ItemService {

    ItemDto create(Long userId, ItemDto itemDto);

    ItemDto read(Long itemId, Long userId);

    ItemDto update(Long ownerId, Long itemId, ItemDto itemDto);

    void delete(Long userId);

    Collection<ItemDto> findAllItemsOfOwner(Long ownerId, PageRequest page);

    Collection<ItemDto> search(String searchRequest, Long userId, PageRequest page);

    CommentDto createComment(Long itemId, Long bookerId, CommentDto commentDto);
}
