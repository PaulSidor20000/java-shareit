package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class ItemDto {
    private Long id;
    @NotBlank(message = "name must be specified")
    private String name;
    @NotBlank(message = "description must be specified")
    private String description;
    @NotNull(message = "availability must be specified")
    private Boolean available;

}
