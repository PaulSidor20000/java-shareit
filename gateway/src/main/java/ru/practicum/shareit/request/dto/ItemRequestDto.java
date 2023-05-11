package ru.practicum.shareit.request.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ItemRequestDto {

    @NotBlank(message = "Description must be specified")
    private String description;

}
