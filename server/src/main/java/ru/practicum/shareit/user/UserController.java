package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserDto create(@RequestBody UserDto userDto) {
        log.info("POST user {}", userDto);
        return userService.create(userDto);
    }

    @GetMapping("/{id}")
    public UserDto read(@PathVariable(value = "id") Long userId) {
        log.info("GET user, userId={}", userId);
        return userService.read(userId);
    }

    @PatchMapping("/{id}")
    public UserDto update(@PathVariable(value = "id") Long userId,
                          @RequestBody UserDto userDto
    ) {
        log.info("PATCH user {}, userId={}", userDto, userId);
        return userService.update(userId, userDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable(value = "id") Long userId) {
        log.info("DELETE user, userId={}", userId);
        userService.delete(userId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping
    public Collection<UserDto> findAll() {
        log.info("GET all users");
        return userService.findAll();
    }

}
