package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.TestEnvironment;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerIT extends TestEnvironment {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ItemService mockItemService;

    @SneakyThrows
    @Test
    void createTest_whenDataValid_thenReturnStatusOk() {
        long ownerId = 2L;
        when(mockItemService.create(ownerId, itemDtoIn)).thenReturn(itemDtoOut);

        String jsonResult = mockMvc.perform(post("/items")
                        .content(objectMapper.writeValueAsString(itemDtoIn))
                        .header("X-Sharer-User-Id", ownerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemDtoOut), jsonResult);
        verify(mockItemService).create(ownerId, itemDtoIn);
    }

    @SneakyThrows
    @Test
    void createTest_whenNameNotValid_thenReturnStatusBadRequest() {
        long ownerId = 2L;
        itemDtoIn.setName("");

        mockMvc.perform(post("/items")
                        .content(objectMapper.writeValueAsString(itemDtoIn))
                        .header("X-Sharer-User-Id", ownerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(mockItemService, never()).create(ownerId, itemDtoIn);
    }

    @SneakyThrows
    @Test
    void createTest_whenDescriptionNotValid_thenReturnStatusBadRequest() {
        long ownerId = 2L;
        itemDtoIn.setDescription("");

        mockMvc.perform(post("/items")
                        .content(objectMapper.writeValueAsString(itemDtoIn))
                        .header("X-Sharer-User-Id", ownerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(mockItemService, never()).create(ownerId, itemDtoIn);
    }

    @SneakyThrows
    @Test
    void createTest_whenAvailableNotValid_thenReturnStatusBadRequest() {
        long ownerId = 2L;
        itemDtoIn.setAvailable(null);

        mockMvc.perform(post("/items")
                        .content(objectMapper.writeValueAsString(itemDtoIn))
                        .header("X-Sharer-User-Id", ownerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(mockItemService, never()).create(ownerId, itemDtoIn);
    }

    @SneakyThrows
    @Test
    void readTest_whenInvoke_thenReturnStatusOk() {
        long itemId = 1L;
        long userId = 1L;
        mockMvc.perform(get("/items/{id}", itemId)
                        .header("X-Sharer-User-Id", userId))
                .andDo(print())
                .andExpect(status().isOk());

        verify(mockItemService).read(itemId, userId);
    }

    @SneakyThrows
    @Test
    void updateTest_whenInvoke_thenReturnStatusOk() {
        long itemId = 1L;
        long ownerId = 1L;
        when(mockItemService.update(ownerId, itemId, itemDtoPatch)).thenReturn(itemDtoPatch);

        String jsonResult = mockMvc.perform(patch("/items/{id}", ownerId)
                        .content(objectMapper.writeValueAsString(itemDtoPatch))
                        .header("X-Sharer-User-Id", ownerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemDtoPatch), jsonResult);
        verify(mockItemService).update(ownerId, itemId, itemDtoPatch);
    }

    @SneakyThrows
    @Test
    void deleteTest_whenInvoke_thenReturnStatusOk() {
        long itemId = 1L;

        mockMvc.perform(MockMvcRequestBuilders.delete("/items/{id}", itemId))
                .andDo(print())
                .andExpect(status().isOk());

        verify(mockItemService).delete(itemId);
    }

    @SneakyThrows
    @Test
    void findAllItemsOfOwnerTest_whenInvoke_thenReturnStatusOk() {
        int from = 0;
        int size = 20;
        long ownerId = 1L;
        when(mockItemService.findAllItemsOfOwner(ownerId, from, size)).thenReturn(List.of(itemDtoOut));

        mockMvc.perform(get("/items/")
                        .header("X-Sharer-User-Id", ownerId)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andDo(print())
                .andExpect(status().isOk());

        verify(mockItemService).findAllItemsOfOwner(ownerId, from, size);
    }

    @SneakyThrows
    @Test
    void searchTest_whenInvoke_thenReturnStatusOk() {
        int from = 0;
        int size = 20;
        String searchRequest = "item1";
        when(mockItemService.search(searchRequest, from, size)).thenReturn(List.of(itemDtoOut));

        mockMvc.perform(get("/items/search")
                        .param("text", searchRequest)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andDo(print())
                .andExpect(status().isOk());

        verify(mockItemService).search(searchRequest, from, size);
    }

    @SneakyThrows
    @Test
    void createCommentTest_whenCommentDataValid_thenReturnStatusOk() {
        long itemId = 1L;
        long bookerId = 1L;
        when(mockItemService.createComment(itemId, bookerId, commentDto)).thenReturn(commentDto);

        String jsonResult = mockMvc.perform(post("/items/{id}/comment", itemId)
                        .content(objectMapper.writeValueAsString(commentDto))
                        .header("X-Sharer-User-Id", bookerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(commentDto), jsonResult);
        verify(mockItemService).createComment(itemId, bookerId, commentDto);
    }

    @SneakyThrows
    @Test
    void createCommentTest_whenCommentNotDataValid_thenReturnStatusBadRequest() {
        long itemId = 1L;
        long bookerId = 1L;
        commentDto.setText("");

        mockMvc.perform(post("/items/{id}/comment", itemId)
                        .content(objectMapper.writeValueAsString(commentDto))
                        .header("X-Sharer-User-Id", bookerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(mockItemService, never()).createComment(itemId, bookerId, commentDto);
    }
}