package ru.practicum.shareit.user.model;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class User {
    private Long id;
    private String name;
    private String email;
    private final Set<Long> itemIds;

}
