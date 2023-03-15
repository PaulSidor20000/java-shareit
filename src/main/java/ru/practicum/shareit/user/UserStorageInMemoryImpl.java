package ru.practicum.shareit.user;

import java.util.*;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

@Slf4j
@Repository("usersInMemory")
public class UserStorageInMemoryImpl implements UserStorage {
    private Long id = 0L;
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public User create(User user) {
        user.setId(makeId());
        users.put(user.getId(), user);
        log.info("New User was successfully created");
        return user;
    }

    @Override
    public User read(Long userId) {
        return users.get(userId);
    }

    @Override
    public User update(User user) {
        users.put(user.getId(), user);
        log.info("The User was successfully updated");
        return user;
    }

    @Override
    public void delete(Long userId) {
        users.remove(userId);
        log.info("The User was successfully deleted");
    }

    @Override
    public Collection<User> findAll() {
        return users.values();
    }

    @Override
    public Optional<User> findUserByEmail(String email) {
        return users.values().stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst();
    }

    @Override
    public boolean checkId(Long userId) {
        return users.containsKey(userId);
    }

    @Override
    public boolean checkEmail(String email) {
        return users.values()
                .stream()
                .anyMatch(user -> user.getEmail().equals(email));
    }

    private Long makeId() {
        return ++id;
    }

}
