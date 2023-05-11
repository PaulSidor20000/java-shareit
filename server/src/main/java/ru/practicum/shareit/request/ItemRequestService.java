package ru.practicum.shareit.request;

import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.Collection;

public interface ItemRequestService {
    ItemRequestDto create(Long userId, ItemRequestDto itemRequestDto);

    Collection<ItemRequestDto> findAllRequestsOfUser(Long userId);

    Collection<ItemRequestDto> findAllRequestsOfOthers(Long userId, PageRequest page);

    ItemRequestDto read(Long requestId, Long userId);
}
