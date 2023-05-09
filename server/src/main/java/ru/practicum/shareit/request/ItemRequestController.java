package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.Collection;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestService itemRequestService;
    private static final String USER_ID = "X-Sharer-User-Id";

    @PostMapping
    public ItemRequestDto create(@RequestHeader(USER_ID) Long userId,
                                 @RequestBody ItemRequestDto itemRequestDto
    ) {
        return itemRequestService.create(userId, itemRequestDto);
    }

    @GetMapping("/{id}")
    public ItemRequestDto read(@RequestHeader(USER_ID) Long userId,
                               @PathVariable(value = "id") Long requestId) {
        return itemRequestService.read(requestId, userId);
    }

    @GetMapping
    public Collection<ItemRequestDto> findAllRequestsOfUser(@RequestHeader(USER_ID) Long userId) {
        return itemRequestService.findAllRequestsOfUser(userId);
    }

    @GetMapping("/all")
    public Collection<ItemRequestDto> findAllRequestsOfOthers(@RequestHeader(USER_ID) Long userId,
                                                              @RequestParam(required = false, defaultValue = "0") Integer from,
                                                              @RequestParam(required = false, defaultValue = "20") Integer size
    ) {
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        return itemRequestService.findAllRequestsOfOthers(userId, page);
    }


}
