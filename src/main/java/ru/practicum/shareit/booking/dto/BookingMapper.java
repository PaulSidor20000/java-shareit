package ru.practicum.shareit.booking.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.model.BookStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

@Mapper(componentModel = "spring",
        imports = BookStatus.class,
        uses = {
                UserMapper.class,
                CommentMapper.class
        })
public interface BookingMapper {


    Booking map(BookingDto bookingDto);

    BookingDto map(Booking booking);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", expression = "java(BookStatus.WAITING)")
    @Mapping(target = "booker", source = "booker")
    @Mapping(target = "item", source = "item")
    Booking merge(User booker, Item item, BookingDto bookingDto);
}
