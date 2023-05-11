package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BookItemRequestDto {
	@NotNull(message = "Item id must be specified")
	private long itemId;

	@NotNull(message = "Start time must be specified")
	@FutureOrPresent(message = "Start time can't be in the past")
	private LocalDateTime start;

	@NotNull(message = "End time must be specified")
	@Future(message = "End time can't be in the past")
	private LocalDateTime end;
}
