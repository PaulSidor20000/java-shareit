package ru.practicum.shareit.item.dto;

import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.item.ItemStorage;
import ru.practicum.shareit.item.model.Item;

@RequiredArgsConstructor
public class ItemDtoMapper {
    private final ItemStorage itemStorage;

    public ItemDto mapToItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .isAvailable(item.isAvailable())
                .build();
    }

    public Item mapToItemModel(ItemDto itemDto) {
        return itemStorage.read(itemDto.getId());
    }

}
