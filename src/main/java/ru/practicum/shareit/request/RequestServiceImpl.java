package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.exceptions.RequestNotValidException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.RequestMapper;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static ru.practicum.shareit.exceptions.ErrorHandler.*;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final RequestMapper requestMapper;
    private final ItemRepository itemRepository;

    @Override
    public ItemRequestDto create(Long userId, ItemRequestDto itemRequestDto) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new EntityNotFoundException(String.format(FAILED_USER_ID, userId)));
        ItemRequest itemRequest = requestMapper.merge(user, itemRequestDto);

        return requestMapper.map(requestRepository.save(itemRequest));
    }

    @Override
    public ItemRequestDto read(Long requestId, Long userId) {
        if (!userIsExist(userId)) {
            throw new EntityNotFoundException(String.format(FAILED_USER_ID, userId));
        }
        ItemRequest itemRequest = requestRepository.findById(requestId).orElseThrow(() ->
                new EntityNotFoundException(FAILED_REQUEST));
        List<Item> items = itemRepository.findAllItemsByRequestIds(Set.of(itemRequest.getId()));

        return requestMapper.merge(items, itemRequest);
    }

    @Override
    public Collection<ItemRequestDto> findAllRequestsOfUser(Long userId) {
        if (!userIsExist(userId)) {
            throw new EntityNotFoundException(String.format(FAILED_USER_ID, userId));
        }
        Map<Long, ItemRequest> requests = requestRepository.findByUserId(userId).stream()
                .collect(Collectors.toMap(ItemRequest::getId, Function.identity()));

        return getItems(requests);
    }

    @Override
    public Collection<ItemRequestDto> findAllRequestsOfOthers(Long userId, Integer from, Integer size) {
        if (from < 0 || size <= 0) {
            throw new RequestNotValidException(FAILED_REQUEST);
        }
        if (!userIsExist(userId)) {
            throw new EntityNotFoundException(String.format(FAILED_USER_ID, userId));
        }
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        Map<Long, ItemRequest> requests = requestRepository.findAllExceptUserId(userId, page).stream()
                .collect(Collectors.toMap(ItemRequest::getId, Function.identity()));

        return getItems(requests);
    }

    private Collection<ItemRequestDto> getItems(Map<Long, ItemRequest> requests) {
        Map<Long, List<Item>> items = itemRepository.findAllItemsByRequestIds(requests.keySet()).stream()
                .collect(Collectors.groupingBy(Item::getRequestId));

        return requests.values().stream()
                .sorted(Comparator.comparing(ItemRequest::getCreated).reversed())
                .map(itemRequest -> requestMapper.merge(items.getOrDefault(itemRequest.getId(), List.of()), itemRequest))
                .collect(Collectors.toList());
    }

    private boolean userIsExist(Long userId) {
        return userRepository.existsById(userId);
    }
}
