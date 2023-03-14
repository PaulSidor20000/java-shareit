package ru.practicum.shareit.user;

import java.util.*;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.MissingObjectException;

@Slf4j
@Repository("usersInMemory")
public class UserStorageInMemoryImpl implements UserStorage {
    private Long id = 0L;
    private final Map<Long, User> users = new HashMap<>();
    private static final String MISSING_USER_ID = "Missing User id: %s";

    @Override
    public User create(User user) {
        user.setId(makeId());
        log.info("New User was successfully created");
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User read(Long userId) {
        if (!users.containsKey(userId)) {
            log.warn(String.format(MISSING_USER_ID, userId));
            throw new MissingObjectException(String.format(MISSING_USER_ID, userId));
        }
        return users.get(userId);
    }

    @Override
    public User update(User user) {
        Long userId = user.getId();
        if (!users.containsKey(userId)) {
            log.warn(String.format(MISSING_USER_ID, userId));
            throw new MissingObjectException(String.format(MISSING_USER_ID, userId));
        }
        log.info("The User successfully updated");
        users.put(userId, user);
        return user;
    }

    @Override
    public void delete(Long userId) {
        log.info("The User successfully deleted");
        users.remove(userId);
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
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
