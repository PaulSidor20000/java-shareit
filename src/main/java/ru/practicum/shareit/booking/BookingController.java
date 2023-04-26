package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;
    public static final String USER_ID = "X-Sharer-User-Id";

    @PostMapping
    public BookingDto makeBooking(@RequestHeader(USER_ID) Long bookerId,
                                  @Valid @RequestBody BookingDto bookingDto) {

        return bookingService.makeBooking(bookerId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approving(@RequestHeader(USER_ID) Long ownerId,
                                @PathVariable Long bookingId,
                                @RequestParam(name = "approved") boolean isApproved) {

        return bookingService.approving(ownerId, bookingId, isApproved);

    }

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@RequestHeader(USER_ID) Long user,
                                 @PathVariable Long bookingId) {

        return bookingService.getBooking(bookingId, user);
    }

    @GetMapping
    public List<BookingDto> getBookerStatistics(@RequestHeader(USER_ID) Long bookerId,
                                                @RequestParam(required = false, defaultValue = "ALL") String state) {

        return bookingService.getBookerStatistics(bookerId, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> getOwnerStatistics(@RequestHeader(USER_ID) Long ownerId,
                                               @RequestParam(required = false, defaultValue = "ALL") String state) {

        return bookingService.getOwnerStatistics(ownerId, state);
    }


}
