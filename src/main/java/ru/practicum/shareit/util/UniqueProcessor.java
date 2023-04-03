package ru.practicum.shareit.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.UserStorage;

import java.lang.reflect.Field;
@Slf4j
@Component
@RequiredArgsConstructor
public class UniqueProcessor implements BeanPostProcessor {
    private UserStorage userStorage;
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Field[] declaredFields = bean.getClass().getDeclaredFields();
        for (Field declaredField : declaredFields) {
            Unique unique = declaredField.getAnnotation(Unique.class);
            if (unique != null) {
                log.info("Found {} Annotation in {} class.", unique.getClass(), bean.getClass());
                log.info(beanName);

            }
        }

        return bean;
    }

}
