package ru.practicum.shareit.item.dto;

import lombok.Data;
import lombok.experimental.Accessors;
import ru.practicum.shareit.booking.dto.BookingShort;

import java.util.Set;

@Accessors(chain = true)
@Data
public class ItemDto {

    private Long id;

    private String name;

    private String description;

    private Boolean available;

    private Long requestId;

    private BookingShort nextBooking;

    private BookingShort lastBooking;

    private Set<CommentDto> comments;

}
