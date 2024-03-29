package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookState;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;
    public static final String USER_ID = "X-Sharer-User-Id";

    @PostMapping
    public BookingDto makeBooking(@RequestHeader(USER_ID) Long bookerId,
                                  @RequestBody BookingDto bookingDto) {
        log.info("POST booking {}, userId={}", bookingDto, bookerId);
        return bookingService.makeBooking(bookerId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approving(@RequestHeader(USER_ID) Long ownerId,
                                @PathVariable Long bookingId,
                                @RequestParam(name = "approved") boolean isApproved
    ) {
        log.info("PATCH booking approving, ownerId={}, bookingId={}, isApproved={}", ownerId, bookingId, isApproved);
        return bookingService.approving(ownerId, bookingId, isApproved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@RequestHeader(USER_ID) Long userId,
                                 @PathVariable Long bookingId) {
        log.info("GET booking {}, userId={}", bookingId, userId);
        return bookingService.getBooking(bookingId, userId);
    }

    @GetMapping
    public List<BookingDto> getBookerStatistics(@RequestHeader(USER_ID) Long bookerId,
                                                @RequestParam(required = false, defaultValue = "ALL") BookState state,
                                                @RequestParam(required = false, defaultValue = "0") Integer from,
                                                @RequestParam(required = false, defaultValue = "20") Integer size
    ) {
        log.info("GET bookings with state {}, bookerId={}, from={}, size={}", state, bookerId, from, size);
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        return bookingService.getBookerStatistics(bookerId, state, page);
    }

    @GetMapping("/owner")
    public List<BookingDto> getOwnerStatistics(@RequestHeader(USER_ID) Long ownerId,
                                               @RequestParam(required = false, defaultValue = "ALL") BookState state,
                                               @RequestParam(required = false, defaultValue = "0") Integer from,
                                               @RequestParam(required = false, defaultValue = "20") Integer size
    ) {
        log.info("GET bookings with state {}, userId={}, from={}, size={}", state, ownerId, from, size);
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        return bookingService.getOwnerStatistics(ownerId, state, page);
    }


}
