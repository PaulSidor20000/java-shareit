package ru.practicum.shareit.exceptions;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@Sql(value = "/testdata.sql")
class ErrorHandlerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    private UserDto userDto;
    private BookingDto bookingDto;

    @BeforeEach
    void setUp() {
        userDto = new UserDto();
        userDto.setName("user1");
        userDto.setEmail("user1@mail.ru");

        bookingDto = new BookingDto();
        bookingDto.setItemId(1L);
        bookingDto.setStart(LocalDateTime.now());
        bookingDto.setEnd(LocalDateTime.now().plusDays(1));
    }

    @Test
    void runtimeExceptionTest_returnsErrorStatus() throws Exception {
        bookingDto.setStart(LocalDateTime.now().plusDays(2));

        mockMvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(bookingDto))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.error", notNullValue()));
    }

    @Test
    void entityNotFoundExceptionTest_returnsErrorStatus() throws Exception {
        mockMvc.perform(get("/users/{id}", 100L))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.error", notNullValue()));
    }

    @Test
    void bookingNotMatchExceptionTest_returnsErrorStatus() throws Exception {
        mockMvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(bookingDto))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.error", notNullValue()));
    }

    @Test
    void emailDuplicateExceptionTest_returnsErrorStatus() throws Exception {
        mockMvc.perform(patch("/users/{id}", 3L)
                        .content(objectMapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.error", notNullValue()));
    }

    @Test
    void dataIntegrityViolationExceptionTest_returnsErrorStatus() throws Exception {
        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.error", notNullValue()));
    }

    @Test
    void throwableTest_returnsServerErrorStatus() throws Exception {
        mockMvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is5xxServerError())
                .andExpect(jsonPath("$.['Server error']", notNullValue()));
    }

}