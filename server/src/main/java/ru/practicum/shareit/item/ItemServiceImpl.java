package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingShort;
import ru.practicum.shareit.booking.model.BookStatus;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static ru.practicum.shareit.exceptions.ErrorHandler.*;

@Service("itemService")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemMapper itemMapper;
    private final CommentMapper commentMapper;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    @Override
    @Transactional
    public ItemDto create(Long ownerId, ItemDto itemDto) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new EntityNotFoundException(String.format(FAILED_OWNER_ID, ownerId)));
        Item item = itemMapper.merge(owner, itemDto);

        return itemMapper.mapOneForOwner(itemRepository.save(item));
    }

    @Override
    public ItemDto read(Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new EntityNotFoundException(String.format(FAILED_ITEM_ID, itemId)));

        if (item.getOwner().getId().equals(userId)) {
            return itemMapper.mapOneForOwner(item);
        }

        return itemMapper.mapForUser(item);
    }

    @Override
    @Transactional
    public ItemDto update(Long ownerId, Long itemId, ItemDto itemDto) {
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new EntityNotFoundException(String.format(FAILED_ITEM_ID, itemId)));
        User owner = item.getOwner();

        if (!owner.getId().equals(ownerId)) {
            throw new EntityNotFoundException(String.format(FAILED_OWNER_ID, ownerId));
        }
        item = itemMapper.merge(item, itemDto);

        return itemMapper.mapOneForOwner(itemRepository.save(item));
    }

    @Override
    @Transactional
    public void delete(Long userId) {
        itemRepository.deleteById(userId);
    }

    @Override
    public List<ItemDto> findAllItemsOfOwner(Long ownerId, PageRequest page) {
        Map<Long, Item> items = itemRepository.findItemsByOwnerIdAndFetchAllEntities(ownerId, page).stream()
                .collect(Collectors.toMap(Item::getId, Function.identity()));

        Map<Long, BookingShort> nextBookings = itemRepository.findNextBookings(items.keySet()).stream()
                .collect(Collectors.toMap(BookingShort::getItemId, Function.identity()));

        Map<Long, BookingShort> lastBookings = itemRepository.findLastBookings(items.keySet()).stream()
                .collect(Collectors.toMap(BookingShort::getItemId, Function.identity()));

        return items.values().stream()
                .map(item -> itemMapper.mapForUser(item)
                        .setNextBooking(nextBookings.get(item.getId()))
                        .setLastBooking(lastBookings.get(item.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> search(String query, PageRequest page) {
        if (query.equals("")) {
            return Collections.emptyList();
        }
        return itemRepository.searchByNameAndDescription(query, page)
                .stream()
                .map(itemMapper::mapForUser)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentDto createComment(Long itemId, Long bookerId, CommentDto commentDto) {
        User booker = userRepository.findById(bookerId).orElseThrow(() ->
                new EntityNotFoundException(String.format(FAILED_USER_ID, bookerId)));
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new EntityNotFoundException(String.format(FAILED_ITEM_ID, itemId)));
        boolean canComment = booker.getBookings().stream()
                .anyMatch(booking ->
                        booking.getItem().equals(item)
                                && booking.getStatus().equals(BookStatus.APPROVED)
                                && booking.getStart().isBefore(LocalDateTime.now()));

        if (canComment) {
            Comment comment = commentMapper.merge(booker, item, commentDto);

            return commentMapper.map(commentRepository.save(comment));
        }

        throw new ValidationException(String.format(FAILED_USER_ID + " can't comment", bookerId));
    }

}
