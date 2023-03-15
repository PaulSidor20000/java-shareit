package ru.practicum.shareit.user;

import java.util.*;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

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
        Long userId = user.getId();
        users.put(userId, user);
        log.info("The User successfully updated");
        return user;
    }

    @Override
    public void delete(Long userId) {
        users.remove(userId);
        log.info("The User successfully deleted");
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User findUserByEmail(String email) {
        return users.values().stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst()
                .orElse(null);
    }

    @Override
    public boolean checkId(Long userId) {
        return users.containsKey(userId);
    }

    @Override
    public boolean checkEmail(String email) {
        return users.values().stream().anyMatch(user -> user.getEmail().equals(email));
    }

    private Long makeId() {
        return ++id;
    }

}
