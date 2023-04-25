package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class BookingShortImpl implements BookingShort {
    private Long id;
    private Long bookerId;
    private Long itemId;
}

