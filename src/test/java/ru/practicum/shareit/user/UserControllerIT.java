package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerIT {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private UserService mockUserService;
    private UserDto userDtoIn, userDtoOut, userDtoPatchName;

    @BeforeEach
    void setUp() {
        userDtoIn = new UserDto();
        userDtoIn.setName("John");
        userDtoIn.setEmail("john@mail.com");

        userDtoOut = new UserDto();
        userDtoOut.setId(1L);
        userDtoOut.setName("John");
        userDtoOut.setEmail("john@mail.com");

        userDtoPatchName = new UserDto();
        userDtoPatchName.setName("Sam");
    }

    @Test
    void createTest_whenDataValid_thenReturnStatusOk() throws Exception {
        when(mockUserService.create(any())).thenReturn(userDtoOut);

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
        verify(mockUserService).create(any());
    }

    @Test
    void createTest_whenNameNotValid_thenReturnStatusBadRequest() throws Exception {
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

    @Test
    void createTest_whenEmailNotValid_thenReturnStatusBadRequest() throws Exception {
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

    @Test
    void readTest_whenInvoke_thenReturnStatusOk() throws Exception {
        long userId = 1L;
        when(mockUserService.read(userId)).thenReturn(userDtoOut);

        mockMvc.perform(get("/users/{id}", userId))
                .andDo(print())
                .andExpect(status().isOk());

        verify(mockUserService).read(userId);
    }

    @Test
    void updateTest_whenInvoke_thenReturnStatusOk() throws Exception {
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

    @Test
    void deleteTest_whenInvoke_thenReturnStatusOk() throws Exception {
        long userId = 1L;

        mockMvc.perform(MockMvcRequestBuilders.delete("/users/{id}", userId))
                .andDo(print())
                .andExpect(status().isOk());

        verify(mockUserService).delete(userId);
    }

    @Test
    void findAllTest_whenInvoke_thenReturnStatusOk() throws Exception {
        when(mockUserService.findAll()).thenReturn(List.of(userDtoOut));

        mockMvc.perform(get("/users"))
                .andDo(print())
                .andExpect(status().isOk());

        verify(mockUserService).findAll();
    }

}