package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemStorage {
    Item create(Item item);

    Item read(Long itemId);

    Item update(Item item);

    void delete(Long itemId);

    Collection<Item> findAllItemsOfOwner(Long ownerId);

    Collection<Item> search(String searchRequest);

    Long findOwnerIdByItemId(Long itemId);

    boolean checkId(Long itemId);

}
