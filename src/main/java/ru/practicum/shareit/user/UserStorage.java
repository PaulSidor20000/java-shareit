package ru.practicum.shareit.user;

import org.springframework.data.repository.CrudRepository;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;

public interface UserStorage extends CrudRepository<User, Long> {

    Optional<User> findUserByEmailContainingIgnoreCase(String email);

}
