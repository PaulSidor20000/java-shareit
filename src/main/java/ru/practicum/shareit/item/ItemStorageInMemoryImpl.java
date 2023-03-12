package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.MissingObjectException;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Repository("itemsInMemory")
public class ItemStorageInMemoryImpl implements ItemStorage {
    private Long id = 0L;
    private final Map<Long, Item> items = new HashMap<>();
    private static final String MISSING_ITEM_ID = "Missing Item id: %s";

    @Override
    public Item create(Item item) {
        Long itemId = makeId();
        item.setId(itemId);
        log.info("New Item successfully created");
        return items.put(itemId, item);
    }

    @Override
    public Item read(Long itemId) {
        if (!items.containsKey(itemId)) {
            log.warn(String.format(MISSING_ITEM_ID, itemId));
            throw new MissingObjectException(String.format(MISSING_ITEM_ID, itemId));
        }
        return items.get(itemId);
    }

    @Override
    public Item update(Item item) {
        Long itemId = item.getId();
        if (!items.containsKey(itemId)) {
            log.warn(String.format(MISSING_ITEM_ID, itemId));
            throw new MissingObjectException(String.format(MISSING_ITEM_ID, itemId));
        }
        log.info("New Item successfully updated");
        return items.put(itemId, item);
    }

    @Override
    public void delete(Long itemId) {
        log.info("New Item successfully deleted");
        items.remove(itemId);
    }

    @Override
    public List<Item> findAll() {
        return null;
    }

    private long makeId() {
        return ++id;
    }

}
