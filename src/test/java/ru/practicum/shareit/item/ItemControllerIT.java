package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerIT {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ItemService mockItemService;
    private CommentDto commentDto;
    private ItemDto itemDto;

    @BeforeEach
    void setUp() {
        itemDto = new ItemDto();
        itemDto.setName("Item1");
        itemDto.setDescription("Item1 Description");
        itemDto.setAvailable(true);

        commentDto = new CommentDto();
        commentDto.setText("First Comment");
    }

    @Test
    void createTest_whenDataValid_thenReturnStatusOk() throws Exception {
        long ownerId = 2L;
        when(mockItemService.create(anyLong(), any(ItemDto.class))).thenReturn(new ItemDto());

        String jsonResult = mockMvc.perform(post("/items")
                        .content(objectMapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", ownerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(new ItemDto()), jsonResult);
        verify(mockItemService).create(anyLong(), any(ItemDto.class));
    }

    @Test
    void createTest_whenNameNotValid_thenReturnStatusBadRequest() throws Exception {
        long ownerId = 2L;
        itemDto.setName("");

        mockMvc.perform(post("/items")
                        .content(objectMapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", ownerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(mockItemService, never()).create(ownerId, itemDto);
    }

    @Test
    void createTest_whenDescriptionNotValid_thenReturnStatusBadRequest() throws Exception {
        long ownerId = 2L;
        itemDto.setDescription("");

        mockMvc.perform(post("/items")
                        .content(objectMapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", ownerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(mockItemService, never()).create(anyLong(), any(ItemDto.class));
    }

    @Test
    void createTest_whenAvailableNotValid_thenReturnStatusBadRequest() throws Exception {
        long ownerId = 2L;
        itemDto.setAvailable(null);

        mockMvc.perform(post("/items")
                        .content(objectMapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", ownerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(mockItemService, never()).create(anyLong(), any(ItemDto.class));
    }

    @Test
    void readTest_whenInvoke_thenReturnStatusOk() throws Exception {
        long itemId = 1L;
        long userId = 1L;
        mockMvc.perform(get("/items/{id}", itemId)
                        .header("X-Sharer-User-Id", userId))
                .andDo(print())
                .andExpect(status().isOk());

        verify(mockItemService).read(anyLong(), anyLong());
    }

    @Test
    void updateTest_whenInvoke_thenReturnStatusOk() throws Exception {
        long itemId = 1L;
        long ownerId = 1L;
        when(mockItemService.update(anyLong(), anyLong(), any(ItemDto.class))).thenReturn(new ItemDto());

        String jsonResult = mockMvc.perform(patch("/items/{id}", itemId)
                        .content(objectMapper.writeValueAsString(new ItemDto()))
                        .header("X-Sharer-User-Id", ownerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(new ItemDto()), jsonResult);
        verify(mockItemService).update(anyLong(), anyLong(), any(ItemDto.class));
    }

    @Test
    void deleteTest_whenInvoke_thenReturnStatusOk() throws Exception {
        long itemId = 1L;

        mockMvc.perform(MockMvcRequestBuilders.delete("/items/{id}", itemId))
                .andDo(print())
                .andExpect(status().isOk());

        verify(mockItemService).delete(itemId);
    }

    @Test
    void findAllItemsOfOwnerTest_whenInvoke_thenReturnStatusOk() throws Exception {
        int from = 0;
        int size = 20;
        long ownerId = 1L;
        when(mockItemService.findAllItemsOfOwner(ownerId, from, size)).thenReturn(List.of(new ItemDto()));

        mockMvc.perform(get("/items/")
                        .header("X-Sharer-User-Id", ownerId)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andDo(print())
                .andExpect(status().isOk());

        verify(mockItemService).findAllItemsOfOwner(ownerId, from, size);
    }

    @Test
    void searchTest_whenInvoke_thenReturnStatusOk() throws Exception {
        int from = 0;
        int size = 20;
        String searchRequest = "item1";
        when(mockItemService.search(searchRequest, from, size)).thenReturn(List.of(new ItemDto()));

        mockMvc.perform(get("/items/search")
                        .param("text", searchRequest)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andDo(print())
                .andExpect(status().isOk());

        verify(mockItemService).search(searchRequest, from, size);
    }

    @Test
    void createCommentTest_whenCommentDataValid_thenReturnStatusOk() throws Exception {
        long itemId = 1L;
        long bookerId = 1L;
        when(mockItemService.createComment(anyLong(), anyLong(), any(CommentDto.class))).thenReturn(new CommentDto());

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

        assertEquals(objectMapper.writeValueAsString(new CommentDto()), jsonResult);
        verify(mockItemService).createComment(anyLong(), anyLong(), any(CommentDto.class));
    }

    @Test
    void createCommentTest_whenCommentNotDataValid_thenReturnStatusBadRequest() throws Exception {
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

        verify(mockItemService, never()).createComment(anyLong(), anyLong(), any(CommentDto.class));
    }
}