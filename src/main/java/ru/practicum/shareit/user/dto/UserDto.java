package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.util.NotDuplicate;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@Builder
public class UserDto {
    private Long id;
    @NotBlank(message = "Name should be specified")
    private String name;
    @Email(regexp = "^[a-z0-9-_.%]+@[a-z0-9-_]+.[a-z]+$", flags = Pattern.Flag.CASE_INSENSITIVE, message = "Email is not valid")
    @NotBlank(message = "Email should be specified")
    @NotDuplicate
    private String email;
}
