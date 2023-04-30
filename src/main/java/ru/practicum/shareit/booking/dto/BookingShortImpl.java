package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BookingShortImpl implements BookingShort {
    private Long id;
    private Long bookerId;
    private Long itemId;
}

