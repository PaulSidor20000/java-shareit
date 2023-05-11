package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;
    private static final String USER_ID = "X-Sharer-User-Id";

    @PostMapping
    public ItemDto create(@RequestHeader(USER_ID) Long ownerId,
                          @RequestBody ItemDto itemDto
    ) {
        log.info("POST item {}, ownerId={}", itemDto, ownerId);
        return itemService.create(ownerId, itemDto);
    }

    @GetMapping("/{id}")
    public ItemDto read(@RequestHeader(USER_ID) Long userId,
                        @PathVariable(value = "id") Long itemId) {
        log.info("GET item, itemId={}, ownerId={}", itemId, userId);
        return itemService.read(itemId, userId);
    }

    @PatchMapping("/{id}")
    public ItemDto update(@RequestHeader(USER_ID) Long ownerId,
                          @PathVariable(value = "id") Long itemId,
                          @RequestBody ItemDto itemDto
    ) {
        log.info("PATCH items {}, ownerId={}, itemId={}", itemDto, ownerId, itemId);
        return itemService.update(ownerId, itemId, itemDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable(value = "id") Long itemId) {
        log.info("DELETE item, itemId={}", itemId);
        itemService.delete(itemId);
        return ResponseEntity.ok("Deleted");
    }

    @GetMapping
    public Collection<ItemDto> findAllItemsOfOwner(@RequestHeader(USER_ID) Long ownerId,
                                                   @RequestParam(required = false, defaultValue = "0") Integer from,
                                                   @RequestParam(required = false, defaultValue = "20") Integer size
    ) {
        log.info("GET items of owner, ownerId={}, from={}, size={}", ownerId, from, size);
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        return itemService.findAllItemsOfOwner(ownerId, page);
    }

    @GetMapping("/search")
    public Collection<ItemDto> search(@RequestHeader(USER_ID) Long userId,
                                      @RequestParam(value = "text") String searchRequest,
                                      @RequestParam(required = false, defaultValue = "0") Integer from,
                                      @RequestParam(required = false, defaultValue = "20") Integer size
    ) {
        log.info("GET search {}, userId={}, from={}, size={}", searchRequest, userId, from, size);
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        return itemService.search(searchRequest, userId, page);
    }

    @PostMapping("/{id}/comment")
    public CommentDto createComment(@RequestHeader(USER_ID) Long bookerId,
                                    @PathVariable(value = "id") Long itemId,
                                    @RequestBody CommentDto commentDto
    ) {
        log.info("POST comment {}, itemId={}, bookerId={}", commentDto, itemId, bookerId);
        return itemService.createComment(itemId, bookerId, commentDto);
    }

}
