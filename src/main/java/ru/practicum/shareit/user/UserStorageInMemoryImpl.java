package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.EmailDuplicateException;
import ru.practicum.shareit.user.model.User;

import java.util.*;

import static ru.practicum.shareit.exceptions.ErrorHandler.DUPLICATED_EMAIL;

@Slf4j
@Repository("usersInMemory")
public class UserStorageInMemoryImpl {//implements UserStorage {
    private Long id = 0L;
    private final Map<Long, User> users = new HashMap<>();

 //   @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

 //   @Override
    public Iterable<User> findAllById(Iterable<Long> longs) {
        return null;
    }

 //   @Override
    public long count() {
        return users.size();
    }

 //   @Override
    public void deleteById(Long aLong) {
        users.remove(aLong);
        log.info("The User was successfully deleted");
    }

 //   @Override
    public void delete(User entity) {
        users.remove(entity.getId());
        log.info("The User was successfully deleted");
    }

 //   @Override
    public void deleteAllById(Iterable<? extends Long> longs) {
        longs.forEach(users::remove);
    }

 //   @Override
    public void deleteAll(Iterable<? extends User> entities) {
        entities.forEach(entity -> users.remove(entity.getId()));
    }

 //   @Override
    public void deleteAll() {
        users.clear();
    }

 //   @Override
    public Optional<User> findUserByEmailContainingIgnoreCase(String email) {
        return users.values().stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst();
    }

  //  @Override
    public <S extends User> S save(S entity) {
        if (entity.getId() == null) {
            entity.setId(++id);
            if (existsByEmail(entity.getEmail())) {
                log.warn(String.format(DUPLICATED_EMAIL, entity.getEmail()));
                throw new EmailDuplicateException(String.format(DUPLICATED_EMAIL, entity.getEmail()));
            }
            users.put(entity.getId(), entity);
            log.info("New User was successfully created");
        } else {
            users.put(entity.getId(), entity);
            log.info("The User was successfully updated");
        }
        return entity;
    }

 //   @Override
    public <S extends User> Iterable<S> saveAll(Iterable<S> entities) {
        return null;
    }

 //   @Override
    public Optional<User> findById(Long aLong) {
        return Optional.ofNullable(users.get(aLong));
    }

 //   @Override
    public boolean existsById(Long userId) {
        return users.containsKey(userId);
    }

    public boolean existsByEmail(String email) {
        return users.values()
                .stream()
                .anyMatch(user -> user.getEmail().equals(email));
    }

}
