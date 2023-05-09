package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;
    private static final String USER_ID = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(USER_ID) Long userId,
                                         @Valid @RequestBody ItemRequestDto itemRequestDto
    ) {
        log.info("POST item request {}, userId={}", itemRequestDto, userId);
        return itemRequestClient.create(userId, itemRequestDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> read(@RequestHeader(USER_ID) Long userId,
                                       @PathVariable(value = "id") Long requestId) {
        log.info("GET items request of user, userId={}, requestId={}", userId, requestId);
        return itemRequestClient.read(requestId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> findAllRequestsOfUser(@RequestHeader(USER_ID) Long userId) {
        log.info("GET items request of user, userId={}", userId);
        return itemRequestClient.findAllRequestsOfUser(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> findAllRequestsOfOthers(
            @RequestHeader(USER_ID) Long userId,
            @PositiveOrZero @RequestParam(required = false, defaultValue = "0") Integer from,
            @Positive @RequestParam(required = false, defaultValue = "20") Integer size
    ) {
        log.info("GET all item requests of others, userId={}, from={}, size={}", userId, from, size);
        return itemRequestClient.findAllRequestsOfOthers(userId, from, size);
    }


}
