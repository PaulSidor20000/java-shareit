package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.EmailDuplicateException;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.stream.Collectors;

import static ru.practicum.shareit.exceptions.ErrorHandler.DUPLICATED_EMAIL;
import static ru.practicum.shareit.exceptions.ErrorHandler.FAILED_USER_ID;

@Service("userService")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDto create(UserDto userDto) {
        User user = UserMapper.mapper.map(userDto);
        return UserMapper.mapper.map(userRepository.save(user));
    }

    @Override
    public UserDto read(Long userId) {
        return userRepository.findById(userId)
                .map(UserMapper.mapper::map)
                .orElseThrow(() -> new EntityNotFoundException(String.format(FAILED_USER_ID, userId)));
    }

    @Override
    @Transactional
    public UserDto update(Long userId, UserDto userDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format(FAILED_USER_ID, userId)));

        userRepository.findByEmailContainingIgnoreCase(userDto.getEmail())
                .ifPresent(userWithEmail -> {
                    if (!userWithEmail.getId().equals(userId)) {
                        throw new EmailDuplicateException(String.format(DUPLICATED_EMAIL, userDto.getEmail()));
                    }
                });

        userDto.setId(userId);
        userDto.setEmail(userDto.getEmail() == null ? user.getEmail() : userDto.getEmail());
        userDto.setName(userDto.getName() == null ? user.getName() : userDto.getName());

        user = UserMapper.mapper.map(userDto);
        return UserMapper.mapper.map(userRepository.save(user));
    }

    @Override
    @Transactional
    public void delete(Long userId) {
        userRepository.deleteById(userId);
    }

    @Override
    public Collection<UserDto> findAll() {
        return userRepository.findAll().stream()
                .map(UserMapper.mapper::map)
                .collect(Collectors.toList());
    }

}
