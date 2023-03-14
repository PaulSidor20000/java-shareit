package ru.practicum.shareit.util;

import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.user.UserStorage;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@RequiredArgsConstructor
public class UserEmailValidator implements ConstraintValidator<NotDuplicate, String> {
    private final UserStorage userStorage;

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        return !userStorage.checkEmail(email);
    }
}
