package ru.practicum.shareit.request.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ItemRequestDto {

    private Long id;

    private String description;

    @DateTimeFormat(pattern = "YYYY-MM-DDTHH:mm:ss")
    private LocalDateTime created;

    private List<ItemDto> items;

}
