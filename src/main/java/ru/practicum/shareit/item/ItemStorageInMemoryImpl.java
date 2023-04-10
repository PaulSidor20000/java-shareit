package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Repository("itemsInMemory")
public class ItemStorageInMemoryImpl {//implements ItemStorage {
    private Long id = 0L;
    private final Map<Long, Item> items = new HashMap<>();

  //  @Override
    public Item create(Item item) {
        item.setId(makeId());
        items.put(item.getId(), item);
        log.info("New Item was successfully created");
        return item;
    }

 //   @Override
    public Item read(Long itemId) {
        return items.get(itemId);
    }

  //  @Override
    public Item update(Item item) {
        items.put(item.getId(), item);
        log.info("The Item was successfully updated");
        return item;
    }

 //   @Override
    public void delete(Long itemId) {
        items.remove(itemId);
        log.info("The Item was successfully deleted");
    }

 //   @Override
    public Collection<Item> findAllItemsOfOwner(Long ownerId) {
        return items.values().stream()
                .filter(item -> item.getOwner().getId().equals(ownerId))
                .collect(Collectors.toList());
    }

 //   @Override
    public Long findOwnerIdByItemId(Long itemId) {
        return items.get(itemId).getOwner().getId();
    }

  //  @Override
    public Collection<Item> search(String searchRequest) {
        final String query = searchRequest.toLowerCase();
        if (query.equals("")) {
            return Collections.emptyList();
        }
        return items.values().stream()
                .filter(item -> item.getName().toLowerCase().contains(query)
                        || item.getDescription().toLowerCase().contains(query))
                .filter(Item::isAvailable)
                .collect(Collectors.toList());
    }

 //   @Override
    public boolean existsById(Long itemId) {
        return items.containsKey(itemId);
    }

    private long makeId() {
        return ++id;
    }

}
