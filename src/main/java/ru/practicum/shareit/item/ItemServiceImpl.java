package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserStorage;

import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
@Service("itemService")
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;
    private final ItemDtoMapper itemDtoMapper;
    private static final String FAILED_ITEM_ID = "Failed Item id: %s";
    private static final String FAILED_OWNER_ID = "Failed owner id: %s";

    @Override
    public ItemDto create(Long ownerId, ItemDto itemDto) {
        if (!userStorage.existsById(ownerId)) {
            log.warn(String.format(FAILED_OWNER_ID, ownerId));
            throw new EntityNotFoundException(String.format(FAILED_OWNER_ID, ownerId));
        }
        Item item = itemDtoMapper.mapToNewItem(ownerId, itemDto);
        return itemDtoMapper.mapToItemDto(itemStorage.create(item));
    }

    @Override
    public ItemDto read(Long itemId) {
        if (itemStorage.existsById(itemId)) {
            return itemDtoMapper.mapToItemDto(itemStorage.read(itemId));
        }
        log.warn(String.format(FAILED_ITEM_ID, itemId));
        throw new EntityNotFoundException(String.format(FAILED_ITEM_ID, itemId));
    }

    @Override
    public ItemDto update(Long ownerId, Long itemId, ItemDto itemDto) {
        if (!itemStorage.existsById(itemId)) {
            log.warn(String.format(FAILED_ITEM_ID, itemId));
            throw new EntityNotFoundException(String.format(FAILED_ITEM_ID, itemId));
        }
        if (!itemStorage.findOwnerIdByItemId(itemId).equals(ownerId)) {
            log.warn(String.format(FAILED_OWNER_ID, ownerId));
            throw new EntityNotFoundException(String.format(FAILED_OWNER_ID, ownerId));
        }
        Item item = itemDtoMapper.mapToItemModel(ownerId, itemId, itemDto);
        return itemDtoMapper.mapToItemDto(itemStorage.update(item));
    }

    @Override
    public void delete(Long userId) {
        itemStorage.delete(userId);
    }

    @Override
    public Collection<ItemDto> findAllItemsOfOwner(Long ownerId) {
        return itemStorage.findAllItemsOfOwner(ownerId).stream()
                .map(itemDtoMapper::mapToItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<ItemDto> search(String searchRequest) {
        return itemStorage.search(searchRequest).stream()
                .map(itemDtoMapper::mapToItemDto)
                .collect(Collectors.toList());
    }

}
