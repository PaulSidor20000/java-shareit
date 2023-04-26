package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final RequestService requestService;
    private static final String USER_ID = "X-Sharer-User-Id";

    @PostMapping
    public ItemRequestDto create(@RequestHeader(USER_ID) Long userId,
                                 @Valid @RequestBody ItemRequestDto itemRequestDto
    ) {
        return requestService.create(userId, itemRequestDto);
    }

    @GetMapping
    public Collection<ItemRequestDto> findAllRequestsOfUser(@RequestHeader(USER_ID) Long userId) {
        return requestService.findAllRequestsOfUser(userId);
    }

    @GetMapping("/all")
    public Collection<ItemRequestDto> findAllRequestsOfOthers(@RequestHeader(USER_ID) Long userId,
                                                              @RequestParam(required = false, defaultValue = "0") Integer from,
                                                              @RequestParam(required = false, defaultValue = "20") Integer size) {
        return requestService.findAllRequestsOfOthers(userId, from, size);
    }

    @GetMapping("/{id}")
    public ItemRequestDto read(@RequestHeader(USER_ID) Long userId,
                               @PathVariable(value = "id") Long requestId) {
        return requestService.read(requestId, userId);
    }


}
