package ru.practicum.shareit.item.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@Mapper(componentModel = "spring",
        uses = {
                CommentMapper.class
        })
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

    public abstract ItemDto mapForUser(Item item);

}
