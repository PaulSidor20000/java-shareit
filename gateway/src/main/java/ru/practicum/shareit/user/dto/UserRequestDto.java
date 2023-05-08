package ru.practicum.shareit.user.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class UserRequestDto {

    private Long id;

    @NotBlank(message = "name must be specified")
    private String name;

    @Email(regexp = "^[a-z0-9-_.%]+@[a-z0-9-_]+.[a-z]+$", flags = Pattern.Flag.CASE_INSENSITIVE, message = "email is not valid")
    @NotBlank(message = "email must be specified")
    private String email;

}
