package ru.practicum.shareit.user.dto;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserMapperTest {
    private final UserMapper userMapper;
    User user, userFromDB;
    UserDto userDtoIn, userDtoOut;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("John");
        user.setEmail("john@mail.com");

        userFromDB = new User();
        userFromDB.setId(1L);
        userFromDB.setName("Mark");
        userFromDB.setEmail("john@mail.com");


        userDtoOut = new UserDto();
        userDtoOut.setId(1L);
        userDtoOut.setName("John");
        userDtoOut.setEmail("john@mail.com");

        userDtoIn = new UserDto();
        userDtoIn.setName("John");

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
        User actual = userMapper.merge(userId, userFromDB, userDtoIn);

        assertEquals(user, actual);
    }

    @Test
    void mergeTest_whenUserDtoAndUserIdIsNullReturnUserFromDB() {
        User actual = userMapper.merge(null, userFromDB, null);

        assertEquals(userFromDB, actual);
    }
}