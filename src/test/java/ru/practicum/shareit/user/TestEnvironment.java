package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

public class TestEnvironment {
    protected UserDto userDtoIn;
    protected UserDto userDtoOut;
    protected UserDto userDtoPatch;
    protected User user;

    @BeforeEach
    void setUp() {
        userDtoPatch = new UserDto();
        userDtoPatch.setName("John");

        userDtoIn = new UserDto();
        userDtoIn.setName("John");
        userDtoIn.setEmail("john@mail.com");

        userDtoOut = new UserDto();
        userDtoOut.setId(1L);
        userDtoOut.setName("John");
        userDtoOut.setEmail("john@mail.com");

        user = new User();
        user.setId(1L);
        user.setName("John");
        user.setEmail("john@mail.com");
    }
}
