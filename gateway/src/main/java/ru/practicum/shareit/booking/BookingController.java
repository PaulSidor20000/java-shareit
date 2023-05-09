package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingClient bookingClient;
    public static final String USER_HEADER_ID = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> makeBooking(@RequestHeader(USER_HEADER_ID) Long userId,
                                              @Valid @RequestBody BookItemRequestDto requestDto) {
        log.info("POST booking {}, userId={}", requestDto, userId);
        if (requestDto.getStart().isAfter(requestDto.getEnd()) || requestDto.getStart().isEqual(requestDto.getEnd())) {
            throw new IllegalArgumentException("Wrong booking time parameter");
        }
        return bookingClient.makeBooking(userId, requestDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approving(@RequestHeader(USER_HEADER_ID) Long ownerId,
                                            @PathVariable Long bookingId,
                                            @RequestParam(name = "approved") Boolean isApproved) {
        log.info("PATCH booking approving, ownerId={}, bookingId={}, isApproved={}", ownerId, bookingId, isApproved);
        return bookingClient.approving(ownerId, bookingId, isApproved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader(USER_HEADER_ID) Long userId,
                                             @PathVariable Long bookingId) {
        log.info("GET booking {}, userId={}", bookingId, userId);
        return bookingClient.getBooking(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getBookerStatistics(
            @RequestHeader(USER_HEADER_ID) Long userId,
            @RequestParam(name = "state", defaultValue = "all") String stateParam,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(name = "size", defaultValue = "10") Integer size
    ) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        log.info("GET bookings with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
        return bookingClient.getBookerStatistics(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getOwnerStatistics(
            @RequestHeader(USER_HEADER_ID) Long ownerId,
            @RequestParam(name = "state", required = false, defaultValue = "ALL") String stateParam,
            @PositiveOrZero @RequestParam(required = false, defaultValue = "0") Integer from,
            @Positive @RequestParam(required = false, defaultValue = "20") Integer size
    ) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        log.info("GET bookings with state {}, userId={}, from={}, size={}", stateParam, ownerId, from, size);
        return bookingClient.getOwnerStatistics(ownerId, state, from, size);
    }

}
