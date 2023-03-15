package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

public interface ItemService {

    ItemDto create(Long userId, ItemDto itemDto);
    ItemDto read(Long itemId);
    ItemDto update(Long ownerId, Long itemId, ItemDto itemDto);
    void delete(Long userId);
    Collection<ItemDto> findAllItemsOfOwner(Long ownerId);
    Collection<ItemDto> search(String searchRequest);
}
