package ru.practicum.shareit.item.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CommentRequestDto {

    @NotBlank(message = "Comment text must be specified")
    private String text;

}
