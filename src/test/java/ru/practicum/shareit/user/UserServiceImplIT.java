package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.TestEnvironment;
import ru.practicum.shareit.user.dto.UserDto;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceImplIT extends TestEnvironment {
    private final UserRepository userRepository;
    private final UserServiceImpl userService;

    @Test
    void updateTest_whenInvoke_thenUpdateUserNameOrEmailAndStoreItInDB() {
        long userId = 1L;
        userRepository.save(user);

        UserDto userDtoActual = userService.update(userId, userDtoPatchName);

        assertEquals(1, userDtoActual.getId());
        assertEquals("Sam", userDtoActual.getName());
        assertEquals("john@mail.com", userDtoActual.getEmail());

        userDtoActual = userService.update(userId, userDtoPatchEmail);

        assertEquals(1, userDtoActual.getId());
        assertEquals("Sam", userDtoActual.getName());
        assertEquals("sam@mail.com", userDtoActual.getEmail());
    }
}