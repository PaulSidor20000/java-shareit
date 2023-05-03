package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.TestEnvironment;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerIT extends TestEnvironment {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ItemRequestService mockItemRequestService;

    @SneakyThrows
    @Test
    void createTest_whenDataValid_thenReturnStatusOk() {
        long userId = 1L;
        when(mockItemRequestService.create(userId, itemRequestDtoIn)).thenReturn(itemRequestDtoOut);

        String jsonResult = mockMvc.perform(post("/requests")
                        .content(objectMapper.writeValueAsString(itemRequestDtoIn))
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemRequestDtoOut), jsonResult);
        verify(mockItemRequestService).create(userId, itemRequestDtoIn);
    }

    @SneakyThrows
    @Test
    void createTest_whenDescriptionNotValid_thenReturnStatusBadRequest() {
        long userId = 1L;
        itemRequestDtoIn.setDescription("");
        when(mockItemRequestService.create(userId, itemRequestDtoIn)).thenReturn(itemRequestDtoOut);

        mockMvc.perform(post("/requests")
                        .content(objectMapper.writeValueAsString(itemRequestDtoIn))
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(mockItemRequestService, never()).create(userId, itemRequestDtoIn);
    }

    @SneakyThrows
    @Test
    void readTest_whenInvoke_thenReturnStatusOk() {
        long userId = 1L;
        long requestId = 1L;
        when(mockItemRequestService.read(userId, requestId)).thenReturn(itemRequestDtoOut);

        mockMvc.perform(get("/requests/{id}", requestId)
                        .header("X-Sharer-User-Id", userId))
                .andDo(print())
                .andExpect(status().isOk());

        verify(mockItemRequestService).read(userId, requestId);
    }

    @SneakyThrows
    @Test
    void findAllRequestsOfUserTest_whenInvoke_thenReturnStatusOk() {
        long userId = 1L;
        when(mockItemRequestService.findAllRequestsOfUser(userId)).thenReturn(List.of(itemRequestDtoOut));

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userId))
                .andDo(print())
                .andExpect(status().isOk());

        verify(mockItemRequestService).findAllRequestsOfUser(userId);
    }

    @SneakyThrows
    @Test
    void findAllRequestsOfOthersTest_whenInvoke_thenReturnStatusOk() {
        long userId = 1L;
        int from = 0;
        int size = 20;
        when(mockItemRequestService.findAllRequestsOfOthers(userId, from, size)).thenReturn(List.of(itemRequestDtoOut));

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andDo(print())
                .andExpect(status().isOk());

        verify(mockItemRequestService).findAllRequestsOfOthers(userId, from, size);
    }

}