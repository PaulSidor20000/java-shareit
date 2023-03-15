package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.EmailDuplicateException;
import ru.practicum.shareit.exceptions.MissingObjectException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoMapper;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
@Service("userService")
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;
    private final UserDtoMapper userDtoMapper;
    private static final String FAILED_USER_ID = "Failed user id: %s";
    private static final String DUPLICATED_EMAIL = "Duplicated email found: %s";

    public UserDto create(UserDto userDto) {
        if (userStorage.checkEmail(userDto.getEmail())) {
            log.warn(String.format(DUPLICATED_EMAIL, userDto.getEmail()));
            throw new EmailDuplicateException(String.format(DUPLICATED_EMAIL, userDto.getEmail()));
        }
        User user = userDtoMapper.mapToNewUser(userDto);
        return userDtoMapper.mapToUserDto(userStorage.create(user));
    }

    public UserDto read(Long userId) {
        if (userStorage.checkId(userId)) {
            return userDtoMapper.mapToUserDto(userStorage.read(userId));
        }
        log.warn(String.format(FAILED_USER_ID, userId));
        throw new MissingObjectException(String.format(FAILED_USER_ID, userId));
    }

    public UserDto update(Long userId, UserDto userDto) {
        if (!userStorage.checkId(userId)) {
            log.warn(String.format(FAILED_USER_ID, userId));
            throw new MissingObjectException(String.format(FAILED_USER_ID, userId));
        }
        userStorage.findUserByEmail(userDto.getEmail())
                .ifPresent(user -> {
                    if (!user.getId().equals(userId)) {
                        log.warn(String.format(DUPLICATED_EMAIL, userDto.getEmail()));
                        throw new EmailDuplicateException(String.format(DUPLICATED_EMAIL, userDto.getEmail()));
                    }
                });
        User user = userDtoMapper.mapToUserModel(userId, userDto);
        return userDtoMapper.mapToUserDto(userStorage.update(user));
    }


    public void delete(Long userId) {
        userStorage.delete(userId);
    }

    public Collection<UserDto> findAll() {
        return userStorage.findAll().stream()
                .map(userDtoMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

}
