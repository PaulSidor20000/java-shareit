package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerIT extends TestEnvironment {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private UserService mockUserService;

    @SneakyThrows
    @Test
    void createTest_whenDataValid_thenReturnStatusOk() {
        when(mockUserService.create(userDtoIn)).thenReturn(userDtoOut);

        String jsonResult = mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userDtoIn))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(userDtoOut), jsonResult);
        verify(mockUserService).create(userDtoIn);
    }

    @SneakyThrows
    @Test
    void createTest_whenNameNotValid_thenReturnStatusBadRequest() {
        userDtoIn.setName("");

        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userDtoIn))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(mockUserService, never()).create(userDtoIn);
    }

    @SneakyThrows
    @Test
    void createTest_whenEmailNotValid_thenReturnStatusBadRequest() {
        userDtoIn.setEmail("");

        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userDtoIn))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(mockUserService, never()).create(userDtoIn);
    }

    @SneakyThrows
    @Test
    void readTest_whenInvoke_thenReturnStatusOk() {
        long userId = 1L;
        mockMvc.perform(get("/users/{id}", userId))
                .andDo(print())
                .andExpect(status().isOk());

        verify(mockUserService).read(userId);
    }

    @SneakyThrows
    @Test
    void updateTest_whenInvoke_thenReturnStatusOk() {
        long userId = 1L;
        when(mockUserService.update(userId, userDtoPatchName)).thenReturn(userDtoOut);

        String jsonResult = mockMvc.perform(patch("/users/{id}", userId)
                        .content(objectMapper.writeValueAsString(userDtoPatchName))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(userDtoOut), jsonResult);
        verify(mockUserService).update(userId, userDtoPatchName);
    }

    @SneakyThrows
    @Test
    void deleteTest_whenInvoke_thenReturnStatusOk() {
        long userId = 1L;
        mockMvc.perform(MockMvcRequestBuilders.delete("/users/{id}", userId))
                .andDo(print())
                .andExpect(status().isOk());

        verify(mockUserService).delete(userId);
    }

}