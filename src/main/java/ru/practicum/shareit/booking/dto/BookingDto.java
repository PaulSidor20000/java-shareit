package ru.practicum.shareit.booking.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.shareit.booking.model.BookStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
public class BookingDto {
    private Long id;

    @NotNull(message = "Item id must be specified")
    private Long itemId;

    @NotNull(message = "Start time must be specified")
    @FutureOrPresent(message = "Start time can't be the past")
    @DateTimeFormat(pattern = "YYYY-MM-DDTHH:mm:ss")
    private LocalDateTime start;

    @NotNull(message = "End time must be specified")
    @Future(message = "End time can't be the past")
    @DateTimeFormat(pattern = "YYYY-MM-DDTHH:mm:ss")
    private LocalDateTime end;

    private BookStatus status;

    private UserDto booker;

    private ItemDto item;

}

