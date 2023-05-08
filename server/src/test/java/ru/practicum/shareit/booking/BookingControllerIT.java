package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerIT {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private BookingService mockBookingService;
    private BookingDto bookingDto;

    @BeforeEach
    void setUp() {
        bookingDto = new BookingDto();
        bookingDto.setItemId(1L);
        bookingDto.setStart(LocalDateTime.now().plusDays(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(2));
    }

    @Test
    void makeBookingTest_whenDataValid_thenReturnStatusOk() throws Exception {
        long bookerId = 1L;
        when(mockBookingService.makeBooking(anyLong(), any(BookingDto.class))).thenReturn(new BookingDto());

        String jsonResult = mockMvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(bookingDto))
                        .header("X-Sharer-User-Id", bookerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(new BookingDto()), jsonResult);
        verify(mockBookingService).makeBooking(anyLong(), any(BookingDto.class));
    }

    @Disabled
    @Test
    void makeBookingTest_whenStartDataNotValid_thenReturnStatusBadRequest() throws Exception {
        long bookerId = 1L;
        bookingDto.setStart(LocalDateTime.now().minusDays(1));
        when(mockBookingService.makeBooking(anyLong(), any(BookingDto.class))).thenReturn(new BookingDto());

        mockMvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(bookingDto))
                        .header("X-Sharer-User-Id", bookerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(mockBookingService, never()).makeBooking(anyLong(), any(BookingDto.class));
    }

    @Disabled
    @Test
    void makeBookingTest_whenEndDataNotValid_thenReturnStatusBadRequest() throws Exception {
        long bookerId = 1L;
        bookingDto.setEnd(LocalDateTime.now().minusDays(1));
        when(mockBookingService.makeBooking(anyLong(), any(BookingDto.class))).thenReturn(new BookingDto());

        mockMvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(bookingDto))
                        .header("X-Sharer-User-Id", bookerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(mockBookingService, never()).makeBooking(anyLong(), any(BookingDto.class));
    }

    @Disabled
    @Test
    void makeBookingTest_whenItemIdNotValid_thenReturnStatusBadRequest() throws Exception {
        long bookerId = 1L;
        bookingDto.setItemId(null);
        when(mockBookingService.makeBooking(anyLong(), any(BookingDto.class))).thenReturn(new BookingDto());

        mockMvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(bookingDto))
                        .header("X-Sharer-User-Id", bookerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(mockBookingService, never()).makeBooking(anyLong(), any(BookingDto.class));
    }

    @Test
    void approvingTest_whenDataValid_thenReturnStatusOk() throws Exception {
        long bookingId = 1L;
        long ownerId = 1L;
        boolean isApproved = true;
        when(mockBookingService.approving(anyLong(), anyLong(), anyBoolean())).thenReturn(new BookingDto());

        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", ownerId)
                        .param("approved", String.valueOf(isApproved)))
                .andDo(print())
                .andExpect(status().isOk());

        verify(mockBookingService).approving(anyLong(), anyLong(), anyBoolean());
    }

    @Test
    void getBookingTest_whenDataValid_thenReturnStatusOk() throws Exception {
        long bookingId = 1L;
        long userId = 1L;
        when(mockBookingService.getBooking(anyLong(), anyLong())).thenReturn(new BookingDto());

        mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId))
                .andDo(print())
                .andExpect(status().isOk());

        verify(mockBookingService).getBooking(anyLong(), anyLong());
    }

    @Test
    void getBookerStatisticsTest_whenDataValid_thenReturnStatusOk() throws Exception {
        long bookerId = 1L;
        int from = 0;
        int size = 20;
        String state = "PAST";
        when(mockBookingService.getBookerStatistics(bookerId, state, from, size)).thenReturn(List.of(new BookingDto()));

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", bookerId)
                        .param("state", state)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andDo(print())
                .andExpect(status().isOk());

        verify(mockBookingService).getBookerStatistics(bookerId, state, from, size);
    }

    @Test
    void getOwnerStatisticsTest_whenDataValid_thenReturnStatusOk() throws Exception {
        long ownerId = 1L;
        int from = 0;
        int size = 20;
        String state = "PAST";
        when(mockBookingService.getOwnerStatistics(ownerId, state, from, size)).thenReturn(List.of(new BookingDto()));

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