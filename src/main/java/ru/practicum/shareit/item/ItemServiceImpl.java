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
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.practicum.shareit.exceptions.ErrorHandler.FAILED_ITEM_ID;
import static ru.practicum.shareit.exceptions.ErrorHandler.FAILED_OWNER_ID;

@Slf4j
@Service("itemService")
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;
    private final ItemDtoMapper itemDtoMapper;

    @Override
    public ItemDto create(Long ownerId, ItemDto itemDto) {
        if (!userStorage.existsById(ownerId)) {
            log.warn(String.format(FAILED_OWNER_ID, ownerId));
            throw new EntityNotFoundException(String.format(FAILED_OWNER_ID, ownerId));
        }
        Item item = itemDtoMapper.mapToNewItem(ownerId, itemDto);
        return itemDtoMapper.mapToItemDto(java.util.Optional.of(itemStorage.save(item)));
    }

    @Override
    public ItemDto read(Long itemId) {
        if (itemStorage.existsById(itemId)) {
            return itemDtoMapper.mapToItemDto(itemStorage.findById(itemId));
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
        if (!findOwnerIdByItemId(itemId).equals(ownerId)) {
            log.warn(String.format(FAILED_OWNER_ID, ownerId));
            throw new EntityNotFoundException(String.format(FAILED_OWNER_ID, ownerId));
        }
        Item item = itemDtoMapper.mapToItemModel(ownerId, itemId, itemDto);
        return itemDtoMapper.mapToItemDto(java.util.Optional.of(itemStorage.save(item)));
    }

    @Override
    public void delete(Long userId) {
        itemStorage.deleteById(userId);
    }

    @Override
    public Collection<ItemDto> findAllItemsOfOwner(Long ownerId) {
        return itemStorage.findAllByOwnerIdAndAvailableTrue(ownerId).stream()
                .map((Item anItem) -> itemDtoMapper.mapToItemDto(Optional.ofNullable(anItem)))
                .collect(Collectors.toList());
    }

    @Override
    public Collection<ItemDto> search(String searchRequest) {
        final String query = "%" + searchRequest.toLowerCase() + "%";
        if (query.equals("%%")) {
            return Collections.emptyList();
        }
        return itemStorage.findAllByNameIsLikeIgnoreCaseOrDescriptionIsLikeIgnoreCaseAndAvailableTrue(query, query)
                .stream()
                .map((Item anItem) -> itemDtoMapper.mapToItemDto(Optional.ofNullable(anItem)))
                .collect(Collectors.toList());
    }

    private Long findOwnerIdByItemId(Long itemId) {
        Optional<Item> anItem = itemStorage.findById(itemId);
        return anItem.map(item -> item.getOwner().getId()).orElseThrow(() ->
                new EntityNotFoundException(String.format(FAILED_ITEM_ID, itemId)));
    }

}
