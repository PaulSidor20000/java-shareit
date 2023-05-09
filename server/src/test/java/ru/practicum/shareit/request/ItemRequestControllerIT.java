package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerIT {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ItemRequestService mockItemRequestService;
    private ItemRequestDto itemRequestDto;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    private final LocalDateTime created = LocalDateTime.parse("2023-08-10T12:00:00", formatter);

    @BeforeEach
    void setUp() {
        itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("Item description");
        itemRequestDto.setCreated(created);
    }

    @Test
    void createTest_whenDataValid_thenReturnStatusOk() throws Exception {
        long userId = 1L;
        when(mockItemRequestService.create(anyLong(), any(ItemRequestDto.class))).thenReturn(new ItemRequestDto());

        String jsonResult = mockMvc.perform(post("/requests")
                        .content(objectMapper.writeValueAsString(itemRequestDto))
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(new ItemRequestDto()), jsonResult);
        verify(mockItemRequestService).create(anyLong(), any(ItemRequestDto.class));
    }

    @Disabled("Validation was removed from this module")
    @Test
    void createTest_whenDescriptionNotValid_thenReturnStatusBadRequest() throws Exception {
        long userId = 1L;
        itemRequestDto.setDescription("");
        when(mockItemRequestService.create(anyLong(), any(ItemRequestDto.class))).thenReturn(new ItemRequestDto());

        mockMvc.perform(post("/requests")
                        .content(objectMapper.writeValueAsString(new ItemRequestDto()))
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(mockItemRequestService, never()).create(anyLong(), any(ItemRequestDto.class));
    }

    @Test
    void readTest_whenInvoke_thenReturnStatusOk() throws Exception {
        long userId = 1L;
        long requestId = 1L;
        when(mockItemRequestService.read(anyLong(), anyLong())).thenReturn(new ItemRequestDto());

        mockMvc.perform(get("/requests/{id}", requestId)
                        .header("X-Sharer-User-Id", userId))
                .andDo(print())
                .andExpect(status().isOk());

        verify(mockItemRequestService).read(anyLong(), anyLong());
    }

    @Test
    void findAllRequestsOfUserTest_whenInvoke_thenReturnStatusOk() throws Exception {
        long userId = 1L;
        when(mockItemRequestService.findAllRequestsOfUser(anyLong())).thenReturn(List.of(new ItemRequestDto()));

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userId))
                .andDo(print())
                .andExpect(status().isOk());

        verify(mockItemRequestService).findAllRequestsOfUser(anyLong());
    }

    @Test
    void findAllRequestsOfOthersTest_whenInvoke_thenReturnStatusOk() throws Exception {
        long userId = 1L;
        int from = 0;
        int size = 20;
        PageRequest page = PageRequest.of(from, size);
        when(mockItemRequestService.findAllRequestsOfOthers(userId, page)).thenReturn(List.of(new ItemRequestDto()));

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andDo(print())
                .andExpect(status().isOk());

        verify(mockItemRequestService).findAllRequestsOfOthers(userId, page);
    }

}