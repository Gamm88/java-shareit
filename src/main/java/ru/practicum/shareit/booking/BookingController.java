package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.model.BookingDto;
import ru.practicum.shareit.booking.service.BookingServiceImpl;

import javax.validation.Valid;
import java.util.Collection;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingServiceImpl bookingService;

    // создать бронирование
    @PostMapping
    public BookingDto createBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                    @Valid @RequestBody BookingDto bookingDto) {
        log.info("ItemController - создание бронирования: {}", bookingDto);

        return bookingService.addBooking(userId, bookingDto);
    }

    // подтверждение или отклонение запроса на бронирование, может быть выполнено только владельцем вещи
    @PatchMapping("/{bookingId}")
    public BookingDto setApprove(@RequestHeader("X-Sharer-User-Id") Long userId,
                                 @RequestParam Boolean approved,
                                 @PathVariable Long bookingId) {
        log.info("ItemController - изменение статуса брони с ИД: {}, пользователем с ИД: {}, статус - {}",
                bookingId, userId, approved);

        return bookingService.setApprove(bookingId, userId, approved);
    }

    // получение данных о конкретном бронировании для автора бронирования, либо владельца вещи
    @GetMapping("/{bookingId}")
    public BookingDto findByBookingIdAndUserId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                               @PathVariable Long bookingId) {
        log.info("ItemController - получение брони с ИД: {}, от пользователя с ИД: {}", bookingId, userId);

        return bookingService.findByBookingIdAndUserId(bookingId, userId);
    }

    // получение списка бронирований для пользователя, по статусу
    @GetMapping
    public Collection<BookingDto> findBookingsByUserIdAndState(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                               @RequestParam(required = false, defaultValue = "ALL")
                                                               String state) {
        log.info("Получен запрос на поиск брони по владельцу с ИД: " + userId + " и статусом: " + state);

        return bookingService.findBookingsByUserIdAndState(userId, state);
    }

    // получение списка бронирований владельца вещи, по статусу
    @GetMapping("/owner")
    public Collection<BookingDto> findBookingsByOwnerIdAndState(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                                                @RequestParam(required = false, defaultValue = "ALL")
                                                                String state) {
        log.info("ItemController - поиск всех броней для пользователя с ИД: {} и статусом - {}", ownerId, state);

        return bookingService.findBookingsByOwnerIdAndState(ownerId, state);
    }
}