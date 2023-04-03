package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.Optional;

public interface ItemStorage extends JpaRepository<Item, Long> {

    Collection<Item> findAllByOwnerIdAndAvailableTrue(Long ownerId);

    Collection<Item> findAllByNameIsLikeIgnoreCaseOrDescriptionIsLikeIgnoreCaseAndAvailableTrue(String queryForName, String queryForDescription);

}
