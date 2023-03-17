package ru.practicum.shareit.user;

import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage {
    User create(User user);

    User read(Long userId);

    User update(User user);

    void delete(Long userId);

    Collection<User> findAll();

    Optional<User> findUserByEmail(String email);

    boolean existsById(Long userId);

    boolean checkEmail(String email);
}
