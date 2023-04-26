package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.Collection;

public interface RequestService {
    ItemRequestDto create(Long userId, ItemRequestDto itemRequestDto);

    Collection<ItemRequestDto> findAllRequestsOfUser(Long userId);

    Collection<ItemRequestDto> findAllRequestsOfOthers(Long userId, Integer from, Integer size);

    ItemRequestDto read(Long requestId, Long userId);
}
