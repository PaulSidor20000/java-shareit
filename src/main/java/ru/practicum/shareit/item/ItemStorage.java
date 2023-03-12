package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {
    Item create(Item item);
    Item read(Long itemId);
    Item update(Item item);
    void delete(Long itemId);
    List<Item> findAll();

}
