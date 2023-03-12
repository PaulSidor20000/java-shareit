package ru.practicum.shareit.user;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import java.util.Collection;

/**
 * TODO Sprint add-controllers.
 */
@Data
@Builder
public class User {
    @Positive
    private Long id;
    @NotBlank
    private String name;
    @Email(regexp = "^[a-z0-9-_.%]+@[a-z0-9-_]+.[a-z]+$", flags = Pattern.Flag.CASE_INSENSITIVE)
    private String email;
    private final Collection<Long> items;

}
