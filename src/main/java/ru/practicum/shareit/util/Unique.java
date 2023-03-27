package ru.practicum.shareit.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value = ElementType.FIELD)
@Retention(value = RetentionPolicy.RUNTIME)
public @interface Unique {
    String message() default "Email exists in database";
    Class<?>[] groups() default {};
//    Class<? extends Payload>[] payload() default {};
}
