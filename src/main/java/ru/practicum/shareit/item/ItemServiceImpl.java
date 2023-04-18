package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.BookStatus;
import ru.practicum.shareit.booking.model.BookingShort;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static ru.practicum.shareit.exceptions.ErrorHandler.*;

@Slf4j
@Service("itemService")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    @Override
    @Transactional
    public ItemDto create(Long ownerId, ItemDto itemDto) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new EntityNotFoundException(String.format(FAILED_OWNER_ID, ownerId)));
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(owner);

        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto read(Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new EntityNotFoundException(String.format(FAILED_ITEM_ID, itemId)));
        ItemDto itemDto = ItemMapper.toItemDto(item);
        itemDto.setComments(commentRepository.findCommentDtosByItem(item));

        if (item.getOwner().getId().equals(userId)) {
            itemDto.setNextBooking(itemRepository.findNextBookingsOfItem(item).stream().findFirst().orElse(null));
            itemDto.setLastBooking(itemRepository.findLastBookingsOfItem(item).stream().findFirst().orElse(null));

            return itemDto;
        }
        return itemDto;
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
        itemDto.setId(itemId);
        itemDto.setName(itemDto.getName() == null ? item.getName() : itemDto.getName());
        itemDto.setDescription(itemDto.getDescription() == null ? item.getDescription() : itemDto.getDescription());
        itemDto.setAvailable(itemDto.getAvailable() == null ? item.isAvailable() : itemDto.getAvailable());

        item = ItemMapper.toItem(itemDto);
        item.setOwner(owner);
        itemDto = ItemMapper.toItemDto(itemRepository.save(item));

        itemDto.setComments(commentRepository.findCommentDtosByItem(item));
        itemDto.setNextBooking(itemRepository.findNextBookingsOfItem(item).stream().findFirst().orElse(null));
        itemDto.setLastBooking(itemRepository.findLastBookingsOfItem(item).stream().findFirst().orElse(null));

        return itemDto;
    }

    @Override
    @Transactional
    public void delete(Long userId) {
        itemRepository.deleteById(userId);
    }

    @Override
    public Collection<ItemDto> findAllItemsOfOwner(Long ownerId) {
        Map<Long, Item> items = itemRepository.findAllByOwnerIdAndAvailableTrue(ownerId).stream()
                .collect(Collectors.toMap(Item::getId, Function.identity()));

        Map<Long, List<CommentDto>> commentDtos = commentRepository.findCommentDtosByItems(items.values()).stream()
                .collect(Collectors.groupingBy(CommentDto::getItemId));

        Collection<BookingShort> nextBookings = itemRepository.findNextBookings(items.values());
        Collection<BookingShort> lastBookings = itemRepository.findLastBookings(items.values());

        return items.values().stream()
                .sorted(Comparator.comparing(Item::getId))
                .map(item -> {
                    ItemDto itemDto = ItemMapper.toItemDto(item);
                    itemDto.setNextBooking(nextBookings.stream()
                            .filter(bookingShort -> bookingShort.getItemId().equals(itemDto.getId()))
                            .findFirst()
                            .orElse(null));
                    itemDto.setLastBooking(lastBookings.stream()
                            .filter(bookingShort -> bookingShort.getItemId().equals(itemDto.getId()))
                            .findFirst()
                            .orElse(null));
                    itemDto.setComments(commentDtos.get(itemDto.getId()));

                    return itemDto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public Collection<ItemDto> search(String searchRequest) {
        final String query = "%" + searchRequest + "%";
        if (query.equals("%%")) {
            return Collections.emptyList();
        }
        return itemRepository.findAllByNameIsLikeIgnoreCaseOrDescriptionIsLikeIgnoreCaseAndAvailableTrue(query, query)
                .stream()
                .map(ItemMapper::toItemDto)
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

        if (!canComment) {
            throw new ValidationException(String.format(FAILED_USER_ID + " can't comment", bookerId));
        }
        Comment comment = Comment.builder()
                .text(commentDto.getText())
                .authorName(booker.getName())
                .created(LocalDateTime.now())
                .item(item)
                .booker(booker)
                .build();
        Long id = commentRepository.save(comment).getId();
        return commentRepository.findCommentDto(id);
    }

}
