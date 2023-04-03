package ru.practicum.shareit.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ServerErrors {
    A_ERROR("error:"),
    LOG_ERROR("error: {}"),
    SERVER_ERROR("Server error:"),
    FAILED_ITEM_ID("Failed Item id: %s"),
    FAILED_OWNER_ID("Failed owner id: %s"),
    FAILED_USER_ID("Failed user id: %s"),
    DUPLICATED_EMAIL("Duplicated email found: %s"),
    ENTITY_NOT_FOUND_MESSAGE("Failed to find an Entity: %s in database");

    private final String message;

}
