package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserDto create(@Valid @RequestBody UserDto userDto) {
        return userService.create(userDto);
    }

    @GetMapping("/{id}")
    public UserDto read(@PathVariable(value = "id") Long userId) {
        return userService.read(userId);
    }

    @PatchMapping("/{id}")
    public UserDto update(@PathVariable(value = "id") Long userId,
                          @RequestBody UserDto userDto
    ) {
        return userService.update(userId, userDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable(value = "id") Long userId) {
        userService.delete(userId);
        return ResponseEntity.ok("Deleted");
    }

    @GetMapping
    public Collection<UserDto> findAll() {
        return userService.findAll();
    }

}
