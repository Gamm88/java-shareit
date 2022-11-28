package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingDto;

import java.util.Collection;

public interface BookingService {
    // создать бронирование
    BookingDto addBooking(Long userId, BookingDto bookingDto);

    // подтверждение или отклонение запроса на бронирование, может быть выполнено только владельцем вещи
    BookingDto setApprove(Long bookingId, Long userId, Boolean approved);

    // получение данных о конкретном бронировании для автора бронирования, либо владельца вещи
    BookingDto findByBookingIdAndUserId(Long bookingId, Long userId);

    // получение списка бронирований для пользователя, по статусу
    Collection<BookingDto> findBookingsByUserIdAndState(Long userId, String state);

    // получение списка бронирований владельца вещи, по статусу
    Collection<BookingDto> findBookingsByOwnerIdAndState(Long ownerId, String state);

    // получение аренды, если не найдена - ошибка 404
    Booking getBookingOrNotFound(Long bookingId);
}
