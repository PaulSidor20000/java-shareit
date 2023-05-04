package ru.practicum.shareit.user.dto;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
@AutoConfigureTestDatabase
@Sql(value = "/testdata.sql")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserMapperTest {
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    User user;
    UserDto userDtoIn, userDtoOut;

    @BeforeEach
    void setUp() {
        user = userRepository.findById(1L).get();

        userDtoOut = new UserDto();
        userDtoOut.setId(1L);
        userDtoOut.setName("user1");
        userDtoOut.setEmail("user1@mail.ru");

        userDtoIn = new UserDto();
        userDtoIn.setName("user1");
    }

    @Test
    void mapTest_UserToUserDto() {
        UserDto actual = userMapper.map(user);

        assertEquals(userDtoOut, actual);
    }

    @Test
    void mapTest_whenUserIsNullReturnUserDtoNull() {
        UserDto actual = userMapper.map((User) null);

        assertNull(actual);
    }

    @Test
    void mapTest_UserDtoToUser() {
        User actual = userMapper.map(userDtoOut);

        assertEquals(user, actual);
    }

    @Test
    void mapTest_whenUserDtoIsNullReturnUserNull() {
        User actual = userMapper.map((UserDto) null);

        assertNull(actual);
    }

    @Test
    void mergeTest_mergeUserDtoToUserFromDBAndReturnUserFromDB() {
        long userId = 1L;
        User actual = userMapper.merge(userId, user, userDtoIn);

        assertEquals(user, actual);
    }

    @Test
    void mergeTest_whenUserDtoAndUserIdIsNullReturnUserFromDB() {
        User actual = userMapper.merge(null, user, null);

        assertEquals(user, actual);
    }
}