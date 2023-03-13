package ru.practicum.shareit.user;

import lombok.Builder;
import lombok.Data;

import java.util.Collection;

@Data
@Builder
public class User {
    private Long id;
    private String name;
    private String email;
    private final Collection<Long> items;

}
