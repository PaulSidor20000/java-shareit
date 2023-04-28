package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exceptions.EmailDuplicateException;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest extends TestEnvironment {
    @Mock
    private UserMapper mockUserMapper;
    @Mock
    private UserRepository mockUserRepository;
    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void createTest_whenInvoke_thenReturnUserDto() {
        when(mockUserMapper.map(userDtoIn)).thenReturn(user);
        when(mockUserRepository.save(user)).thenReturn(user);
        when(mockUserMapper.map(user)).thenReturn(userDtoOut);

        UserDto actualDto = userService.create(userDtoIn);

        assertEquals(userDtoOut, actualDto);
        verify(mockUserRepository).save(user);
    }

    @Test
    void readTest_whenUserFound_thenReturnUserDTO() {
        long userId = 1L;
        when(mockUserRepository.findById(userId)).thenReturn(Optional.of(user));
        when(mockUserMapper.map(user)).thenReturn(userDtoOut);

        UserDto actualDto = userService.read(userId);

        assertEquals(userDtoOut, actualDto);
    }

    @Test
    void readTest_whenUserNotFound_thenEntityNotFoundExceptionThrown() {
        long userId = 1L;
        when(mockUserRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.read(userId));
    }

    @Test
    void updateTest_whenInputDataValid_thenReturnUpdatedUserDTO() {
        long userId = 1L;
        when(mockUserRepository.findById(userId)).thenReturn(Optional.of(user));
        when(mockUserRepository.findByEmailContainingIgnoreCase(userDtoIn.getEmail())).thenReturn(Optional.of(user));
        when(mockUserMapper.merge(userId, user, userDtoIn)).thenReturn(user);
        when(mockUserRepository.save(user)).thenReturn(user);
        when(mockUserMapper.map(user)).thenReturn(userDtoOut);

        UserDto actualDto = userService.update(userId, userDtoIn);

        assertEquals(userDtoOut, actualDto);
        verify(mockUserRepository).save(user);
    }

    @Test
    void updateTest_whenUserNotFound_thenEntityNotFoundExceptionThrown() {
        long userId = 2L;
        when(mockUserRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.update(userId, userDtoIn));
        verify(mockUserRepository, never()).save(user);
    }

    @Test
    void updateTest_whenEmailExist_thenEmailDuplicateExceptionThrown() {
        long userId = 2L;
        when(mockUserRepository.findById(userId)).thenReturn(Optional.of(user));
        when(mockUserRepository.findByEmailContainingIgnoreCase(userDtoIn.getEmail())).thenReturn(Optional.of(user));

        assertThrows(EmailDuplicateException.class, () -> userService.update(userId, userDtoIn));
        verify(mockUserRepository, never()).save(user);
    }

    @Test
    void deleteTest_whenInvoke_thenCheckInvocationOfIt() {
        long userId = 1L;

        userService.delete(userId);

        verify(mockUserRepository).deleteById(userId);
    }

    @Test
    void findAllTest_whenInvoke_thenReturnListOfUsers() {
        when(mockUserRepository.findAll()).thenReturn(List.of(user));
        when(mockUserMapper.map(user)).thenReturn(userDtoOut);

        Collection<UserDto> actualUsers = userService.findAll();

        assertEquals(List.of(userDtoOut), actualUsers);
    }
}