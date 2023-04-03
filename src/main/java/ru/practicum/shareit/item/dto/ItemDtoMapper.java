package ru.practicum.shareit.item.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.ItemStorage;
import ru.practicum.shareit.item.model.Item;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ItemDtoMapper {
    private final ItemStorage itemStorage;

    public ItemDto mapToItemDto(Optional<Item> anItem) {
        return anItem.map(item -> ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build()).orElse(null);
    }

    public Item mapToItemModel(Long ownerId, Long itemId, ItemDto itemDto) {
        Optional<Item> anItem = itemStorage.findById(itemId);

        return anItem.map(item -> Item.builder()
                .id(itemId)
                .name(itemDto.getName() == null ? item.getName() : itemDto.getName())
                .description(itemDto.getDescription() == null ? item.getDescription() : itemDto.getDescription())
                .available(itemDto.getAvailable() == null ? item.getAvailable() : itemDto.getAvailable())
                .ownerId(ownerId)
                .build()).orElse(null);
    }

    public Item mapToNewItem(Long ownerId, ItemDto itemDto) {
        return Item.builder()
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .ownerId(ownerId)
                .build();
    }

}
