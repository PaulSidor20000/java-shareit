package ru.practicum.shareit.booking;

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
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerIT extends TestEnvironment {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private BookingService mockBookingService;

    @SneakyThrows
    @Test
    void makeBookingTest_whenDataValid_thenReturnStatusOk() {
        long bookerId = 1L;
        when(mockBookingService.makeBooking(bookerId, bookingDtoInFut)).thenReturn(bookingDtoInFut);

        String jsonResult = mockMvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(bookingDtoInFut))
                        .header("X-Sharer-User-Id", bookerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(bookingDtoInFut), jsonResult);
        verify(mockBookingService).makeBooking(bookerId, bookingDtoInFut);
    }

    @SneakyThrows
    @Test
    void makeBookingTest_whenStartDataNotValid_thenReturnStatusBadRequest() {
        long bookerId = 1L;
        bookingDtoInFut.setStart(LocalDateTime.now().minusDays(1));
        when(mockBookingService.makeBooking(bookerId, bookingDtoInFut)).thenReturn(bookingDtoInFut);

        mockMvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(bookingDtoInFut))
                        .header("X-Sharer-User-Id", bookerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(mockBookingService, never()).makeBooking(bookerId, bookingDtoInFut);
    }

    @SneakyThrows
    @Test
    void makeBookingTest_whenEndtDataNotValid_thenReturnStatusBadRequest() {
        long bookerId = 1L;
        bookingDtoInFut.setEnd(LocalDateTime.now().minusDays(1));
        when(mockBookingService.makeBooking(bookerId, bookingDtoInFut)).thenReturn(bookingDtoInFut);

        mockMvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(bookingDtoInFut))
                        .header("X-Sharer-User-Id", bookerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(mockBookingService, never()).makeBooking(bookerId, bookingDtoInFut);
    }

    @SneakyThrows
    @Test
    void makeBookingTest_whenItemIdNotValid_thenReturnStatusBadRequest() {
        long bookerId = 1L;
        bookingDtoInFut.setItemId(null);
        when(mockBookingService.makeBooking(bookerId, bookingDtoInFut)).thenReturn(bookingDtoInFut);

        mockMvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(bookingDtoInFut))
                        .header("X-Sharer-User-Id", bookerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(mockBookingService, never()).makeBooking(bookerId, bookingDtoInFut);
    }


    @SneakyThrows
    @Test
    void approvingTest_whenDataValid_thenReturnStatusOk() {
        long bookingId = 1L;
        long ownerId = 1L;
        boolean isApproved = true;
        when(mockBookingService.approving(ownerId, bookingId, isApproved)).thenReturn(bookingDtoInFut);

        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", ownerId)
                        .param("approved", String.valueOf(isApproved)))
                .andDo(print())
                .andExpect(status().isOk());

        verify(mockBookingService).approving(ownerId, bookingId, isApproved);
    }

    @SneakyThrows
    @Test
    void getBookingTest_whenDataValid_thenReturnStatusOk() {
        long bookingId = 1L;
        long userId = 1L;
        when(mockBookingService.getBooking(userId, bookingId)).thenReturn(bookingDtoInFut);

        mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId))
                .andDo(print())
                .andExpect(status().isOk());

        verify(mockBookingService).getBooking(userId, bookingId);
    }

    @SneakyThrows
    @Test
    void getBookerStatisticsTest_whenDataValid_thenReturnStatusOk() {
        long bookerId = 1L;
        int from = 0;
        int size = 20;
        String state = "PAST";
        when(mockBookingService.getBookerStatistics(bookerId, state, from, size)).thenReturn(List.of(bookingDtoInFut));

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", bookerId)
                        .param("state", state)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andDo(print())
                .andExpect(status().isOk());

        verify(mockBookingService).getBookerStatistics(bookerId, state, from, size);
    }

    @SneakyThrows
    @Test
    void getOwnerStatisticsTest_whenDataValid_thenReturnStatusOk() {
        long ownerId = 1L;
        int from = 0;
        int size = 20;
        String state = "PAST";
        when(mockBookingService.getOwnerStatistics(ownerId, state, from, size)).thenReturn(List.of(bookingDtoInFut));

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", ownerId)
                        .param("state", state)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andDo(print())
                .andExpect(status().isOk());

        verify(mockBookingService).getOwnerStatistics(ownerId, state, from, size);
    }

}