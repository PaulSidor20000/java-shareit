package ru.practicum.shareit.booking.dto;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.booking.model.Booking;

@Mapper
public interface BookingMapper {
    BookingMapper mapper = Mappers.getMapper(BookingMapper.class);

    Booking map(BookingDto bookingDto);

    BookingDto map(Booking booking);
}
