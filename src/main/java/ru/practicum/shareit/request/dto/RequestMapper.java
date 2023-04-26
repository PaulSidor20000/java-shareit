package ru.practicum.shareit.request.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = "spring", imports = LocalDateTime.class)
public interface RequestMapper {

    ItemRequestDto map(ItemRequest itemRequest);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "created", expression = "java(LocalDateTime.now())")
    ItemRequest merge(User user, ItemRequestDto itemRequestDto);

    ItemRequestDto merge(List<Item> items, ItemRequest itemRequest);
}
