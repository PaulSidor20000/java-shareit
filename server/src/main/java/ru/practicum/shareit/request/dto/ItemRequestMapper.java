package ru.practicum.shareit.request.dto;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Mapper(
        componentModel = "spring",
        imports = LocalDateTime.class,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        uses = ItemMapper.class
)
public interface ItemRequestMapper {

    ItemRequestDto map(ItemRequest itemRequest);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "created", expression = "java(LocalDateTime.now())")
    ItemRequest merge(User user, ItemRequestDto itemRequestDto);

    ItemRequestDto merge(List<Item> items, ItemRequest itemRequest);
}
