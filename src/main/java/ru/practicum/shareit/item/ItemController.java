package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
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
                          @Valid @RequestBody ItemDto itemDto
    ) {
        return itemService.create(ownerId, itemDto);
    }

    @GetMapping("/{id}")
    public ItemDto read(@RequestHeader(USER_ID) Long userId,
                        @PathVariable(value = "id") Long itemId) {
        return itemService.read(itemId, userId);
    }

    @PatchMapping("/{id}")
    public ItemDto update(@RequestHeader(USER_ID) Long ownerId,
                          @PathVariable(value = "id") Long itemId,
                          @RequestBody ItemDto itemDto
    ) {
        return itemService.update(ownerId, itemId, itemDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable(value = "id") Long itemId) {
        itemService.delete(itemId);
        return ResponseEntity.ok("Deleted");
    }

    @GetMapping
    public Collection<ItemDto> findAllItemsOfOwner(@RequestHeader(USER_ID) Long ownerId,
                                                   @RequestParam(required = false, defaultValue = "0") Integer from,
                                                   @RequestParam(required = false, defaultValue = "20") Integer size
    ) {
        return itemService.findAllItemsOfOwner(ownerId, from, size);
    }

    @GetMapping("/search")
    public Collection<ItemDto> search(@RequestParam(value = "text") String searchRequest,
                                      @RequestParam(required = false, defaultValue = "0") Integer from,
                                      @RequestParam(required = false, defaultValue = "20") Integer size
    ) {
        return itemService.search(searchRequest, from, size);
    }

    @PostMapping("/{id}/comment")
    public CommentDto createComment(@RequestHeader(USER_ID) Long bookerId,
                                    @PathVariable(value = "id") Long itemId,
                                    @Valid @RequestBody CommentDto commentDto
    ) {
        return itemService.createComment(itemId, bookerId, commentDto);
    }

}
