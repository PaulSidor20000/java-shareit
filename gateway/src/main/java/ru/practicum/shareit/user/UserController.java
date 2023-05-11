package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserRequestDto;

import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {
    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody UserRequestDto requestDto) {
        log.info("POST user {}", requestDto);
        return userClient.create(requestDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> read(@PathVariable(value = "id") Long userId) {
        log.info("GET user, userId={}", userId);
        return userClient.read(userId);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable(value = "id") Long userId,
                                         @RequestBody UserRequestDto requestDto
    ) {
        log.info("PATCH user {}, userId={}", requestDto, userId);
        return userClient.update(userId, requestDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable(value = "id") Long userId) {
        log.info("DELETE user, userId={}", userId);
        userClient.deleteById(userId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<Object> findAll() {
        log.info("GET all users");
        return userClient.findAll();
    }

}
