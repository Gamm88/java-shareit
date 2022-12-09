package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.State;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    // создать бронирование
    @PostMapping
    public ResponseEntity<Object> addBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @Valid @RequestBody BookingDto bookingDto) {
        log.info("BookingController - создание бронирования: {}", bookingDto);

        return bookingClient.addBooking(userId, bookingDto);
    }

    // подтверждение или отклонение запроса на бронирование, может быть выполнено только владельцем вещи
    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> setApprove(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @RequestParam boolean approved,
                                             @PathVariable Long bookingId) {
        log.info("BookingController - изменение статуса брони с ИД: {}, пользователем с ИД: {}, статус - {}",
                bookingId, userId, approved);

        return bookingClient.setApprove(bookingId, userId, approved);
    }

    // получение данных о конкретном бронировании для автора бронирования, либо владельца вещи
    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> findByBookingIdAndUserId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                           @PathVariable Long bookingId) {
        log.info("BookingController - получение брони с ИД: {}, от пользователя с ИД: {}", userId, bookingId);

        return bookingClient.getBooking(userId, bookingId);
    }

    // получение списка бронирований для пользователя, по статусу
    @GetMapping
    public ResponseEntity<Object> findBookingsByUserIdAndState(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                               @RequestParam(name = "state", defaultValue = "ALL") String stateParam,
                                                               @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                               @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        State state = State.getStateOrValidatorExceptions(stateParam);
        log.info("BookingController - Получен запрос на поиск брони по владельцу с ИД: " + userId + " и статусом: " + state);

        return bookingClient.findBookingsByUserIdAndState(userId, state, from, size);
    }

    // получение списка бронирований владельца вещи, по статусу
    @GetMapping("/owner")
    public ResponseEntity<Object> findBookingsByOwnerIdAndState(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                                                @RequestParam(name = "state", defaultValue = "ALL") String stateParam,
                                                                @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                                @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        State state = State.getStateOrValidatorExceptions(stateParam);
        log.info("BookingController - поиск всех броней для пользователя с ИД: {} и статусом - {}", ownerId, stateParam);

        return bookingClient.findBookingsByOwnerIdAndState(ownerId, state, from, size);
    }
}