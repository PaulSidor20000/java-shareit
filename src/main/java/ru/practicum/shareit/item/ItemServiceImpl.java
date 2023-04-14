package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserStorage;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.practicum.shareit.exceptions.ErrorHandler.*;

@Slf4j
@Service("itemService")
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;
    private final CommentRepository commentRepository;
    private final ItemDtoMapper itemDtoMapper;

    @Override
    @Transactional
    public ItemDto create(Long ownerId, ItemDto itemDto) {
        if (!userStorage.existsById(ownerId)) {
            log.warn(String.format(FAILED_OWNER_ID, ownerId));
            throw new EntityNotFoundException(String.format(FAILED_OWNER_ID, ownerId));
        }
        Item item = itemDtoMapper.mapToNewItem(ownerId, itemDto);
        return itemDtoMapper.mapToItemDtoForUser(java.util.Optional.of(itemStorage.save(item)));
    }

    @Override
    public ItemDto read(Long itemId, Long userId) {
        Item item = itemStorage.findById(itemId).orElseThrow(() ->
                new EntityNotFoundException(String.format(FAILED_ITEM_ID, itemId)));

        if (item.getOwner().getId().equals(userId)) {
            return itemDtoMapper.mapToItemDtoForOwner(itemStorage.findById(itemId));
        } else {
            return itemDtoMapper.mapToItemDtoForUser(itemStorage.findById(itemId));
        }
    }

    @Override
    @Transactional
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
        return itemDtoMapper.mapToItemDtoForUser(java.util.Optional.of(itemStorage.save(item)));
    }

    @Override
    @Transactional
    public void delete(Long userId) {
        itemStorage.deleteById(userId);
    }

    @Override
    public Collection<ItemDto> findAllItemsOfOwner(Long ownerId) {
        return itemStorage.findAllByOwnerIdAndAvailableTrue(ownerId).stream()
                .sorted(Comparator.comparing(Item::getId))
                .map(item -> itemDtoMapper.mapToItemDtoForOwner(Optional.of(item)))
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
                .map((Item anItem) -> itemDtoMapper.mapToItemDtoForUser(Optional.ofNullable(anItem)))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentDto createComment(Long itemId, Long bookerId, CommentDto commentDto) {
        User booker = userStorage.findById(bookerId).orElseThrow(() ->
                new EntityNotFoundException(String.format(FAILED_USER_ID, bookerId)));
        Item item = itemStorage.findById(itemId).orElseThrow(() ->
                new EntityNotFoundException(String.format(FAILED_ITEM_ID, itemId)));

        if (booker.getBookings().stream().noneMatch(booking -> booking.getItem().equals(item))) {
            throw new ValidationException(String.format(FAILED_USER_ID, bookerId));
        }
        Comment comment = Comment.builder()
                .text(commentDto.getText())
                .authorName(booker.getName())
                .created(LocalDateTime.now())
                .item(item)
                .booker(booker)
                .build();
        Long id = commentRepository.save(comment).getId();
        return commentRepository.findDto(id);
    }

    private Long findOwnerIdByItemId(Long itemId) {
        Optional<Item> anItem = itemStorage.findById(itemId);
        return anItem.map(item -> item.getOwner().getId()).orElseThrow(() ->
                new EntityNotFoundException(String.format(FAILED_ITEM_ID, itemId)));
    }

}
