package ru.practicum.shareit.user;

import java.util.List;

public interface UserStorage {
    User create(User user);
    User read(Long userId);
    User update(User user);
    void delete(Long userId);
    List<User> findAll();
}
