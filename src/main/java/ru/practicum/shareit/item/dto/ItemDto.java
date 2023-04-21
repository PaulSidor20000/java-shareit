package ru.practicum.shareit.item.dto;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.booking.model.BookingShort;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Collection;

@Getter
@Setter
public class ItemDto {

    private Long id;

    @NotBlank(message = "name must be specified")
    private String name;

    @NotBlank(message = "description must be specified")
    private String description;

    @NotNull(message = "availability must be specified")
    private Boolean available;

    private BookingShort nextBooking;

    private BookingShort lastBooking;

    private Collection<CommentDto> comments;

}
