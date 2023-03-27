package ru.practicum.shareit.util;

import ru.practicum.shareit.user.dto.UserDto;

import java.lang.annotation.Annotation;
import java.util.Arrays;

public class UniqueProcessor {
    Class<?> aClass = UserDto.class;
    Annotation[] annotations = aClass.getAnnotations();
    for (Annotation annotation : annotations) {

    }
    Arrays.stream()

}
