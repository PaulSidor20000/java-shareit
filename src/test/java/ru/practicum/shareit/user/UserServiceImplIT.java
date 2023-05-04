package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@SpringBootTest
@AutoConfigureTestDatabase
@Sql(value = "/testdata.sql")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceImplIT {
    private final UserServiceImpl userService;
    private UserDto userDtoPatchName, userDtoPatchEmail;

    @BeforeEach
    void setUp() {
        userDtoPatchName = new UserDto();
        userDtoPatchName.setName("Sam");

        userDtoPatchEmail = new UserDto();
        userDtoPatchEmail.setEmail("sam@mail.com");
    }

    @Test
    void updateTest_whenInvoke_thenUpdateUserNameOrEmailAndStoreItInDB() {
        long userId = 1L;

        UserDto userDtoActual = userService.update(userId, userDtoPatchName);

        assertEquals(1, userDtoActual.getId());
        assertEquals("Sam", userDtoActual.getName());
        assertEquals("user1@mail.ru", userDtoActual.getEmail());

        userDtoActual = userService.update(userId, userDtoPatchEmail);

        assertEquals(1, userDtoActual.getId());
        assertEquals("Sam", userDtoActual.getName());
        assertEquals("sam@mail.com", userDtoActual.getEmail());
    }
}