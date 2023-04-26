package ru.practicum.shareit.item.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import ru.practicum.shareit.booking.dto.BookingShort;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.Collection;

@Accessors(chain = true)
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

    @Positive(message = "Value must be positive")
    private Long requestId;

    private BookingShort nextBooking;

    private BookingShort lastBooking;

    private Collection<CommentDto> comments;

}
