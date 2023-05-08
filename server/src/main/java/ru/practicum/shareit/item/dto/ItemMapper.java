package ru.practicum.shareit.item.dto;

import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Component
@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        uses = CommentMapper.class
)
public abstract class ItemMapper {
    @Autowired
    protected ItemRepository itemRepository;

    @Mapping(target = "id", source = "itemDto.id")
    @Mapping(target = "name", source = "itemDto.name")
    @Mapping(target = "bookings", ignore = true)
    @Mapping(target = "comments", ignore = true)
    public abstract Item merge(User owner, ItemDto itemDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", expression = "java(itemDto.getName() == null ? itemFromDB.getName() : itemDto.getName())")
    @Mapping(target = "description", expression = "java(itemDto.getDescription() == null ? itemFromDB.getDescription() : itemDto.getDescription())")
    @Mapping(target = "available", expression = "java(itemDto.getAvailable() == null ? itemFromDB.isAvailable() : itemDto.getAvailable())")
    @Mapping(target = "bookings", ignore = true)
    @Mapping(target = "comments", ignore = true)
    public abstract Item merge(@MappingTarget Item itemFromDB, ItemDto itemDto);

    @Mapping(target = "nextBooking", expression = "java(itemRepository.findNextBookingsOfItem(item.getId()).orElse(null))")
    @Mapping(target = "lastBooking", expression = "java(itemRepository.findLastBookingsOfItem(item.getId()).orElse(null))")
    public abstract ItemDto mapOneForOwner(Item item);

    @Named("mapForUser")
    public abstract ItemDto mapForUser(Item item);

    public abstract List<ItemDto> map(List<Item> items);

    public abstract Item map(ItemDto itemDto);

}
