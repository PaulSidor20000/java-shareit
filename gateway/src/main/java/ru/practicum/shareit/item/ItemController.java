package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemClient itemClient;
    private static final String USER_ID = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(USER_ID) Long ownerId,
                                         @Valid @RequestBody ItemRequestDto requestDto
    ) {
        log.info("POST item {}, ownerId={}", requestDto, ownerId);
        return itemClient.create(ownerId, requestDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> read(@RequestHeader(USER_ID) Long userId,
                                       @PathVariable(value = "id") Long itemId) {
        log.info("GET item, itemId={}, ownerId={}", itemId, userId);
        return itemClient.read(itemId, userId);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> update(@RequestHeader(USER_ID) Long ownerId,
                                         @PathVariable(value = "id") Long itemId,
                                         @RequestBody ItemRequestDto requestDto
    ) {
        log.info("PATCH items {}, ownerId={}, itemId={}", requestDto, ownerId, itemId);
        return itemClient.update(ownerId, itemId, requestDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable(value = "id") Long itemId) {
        log.info("DELETE item, itemId={}", itemId);
        itemClient.deleteById(itemId);
        return ResponseEntity.ok("Deleted");
    }

    @GetMapping
    public ResponseEntity<Object> findAllItemsOfOwner(@RequestHeader(USER_ID) Long ownerId,
                                                      @PositiveOrZero @RequestParam(required = false, defaultValue = "0") Integer from,
                                                      @Positive @RequestParam(required = false, defaultValue = "20") Integer size
    ) {
        log.info("GET items of owner, ownerId={}, from={}, size={}", ownerId, from, size);
        return itemClient.findAllItemsOfOwner(ownerId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestHeader(USER_ID) Long userId,
                                         @NotBlank @RequestParam(value = "text") String searchRequest,
                                         @PositiveOrZero @RequestParam(required = false, defaultValue = "0") Integer from,
                                         @Positive @RequestParam(required = false, defaultValue = "20") Integer size
    ) {
        log.info("GET search {}, userId={}, from={}, size={}", searchRequest, userId, from, size);
        return itemClient.search(searchRequest, userId, from, size);
    }

    @PostMapping("/{id}/comment")
    public ResponseEntity<Object> createComment(@RequestHeader(USER_ID) Long bookerId,
                                                @PathVariable(value = "id") Long itemId,
                                                @Valid @RequestBody CommentRequestDto requestDto
    ) {
        log.info("POST comment {}, itemId={}, bookerId={}", requestDto, itemId, bookerId);
        return itemClient.createComment(itemId, bookerId, requestDto);
    }

}
