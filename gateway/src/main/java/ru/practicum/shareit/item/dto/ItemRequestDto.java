package ru.practicum.shareit.item.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
public class ItemRequestDto {

    @NotBlank(message = "name must be specified")
    private String name;

    @NotBlank(message = "description must be specified")
    private String description;

    @NotNull(message = "availability must be specified")
    private Boolean available;

    @Positive(message = "Value must be positive")
    private Long requestId;
}
