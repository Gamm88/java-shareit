package ru.practicum.shareit.booking.service;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.booking.model.*;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.item.Item;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exception.ValidatorExceptions;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.servise.UserService;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemService itemService;

    // создать бронирование
    public BookingDto addBooking(Long userId, BookingDto bookingDto) {
        Item item = itemService.getItemOrNotFound(bookingDto.getItemId());
        User user = userService.getUserOrNotFound(userId);

        if (!item.getAvailable()) {
            throw new ValidatorExceptions("Вещь недоступна для аренды");
        }
        if (userId == item.getOwner()) {
            throw new NotFoundException("Владелец вещи не может бронировать свою вещь");
        }
        if (bookingDto.getEnd().isBefore(bookingDto.getStart())) {
            throw new ValidatorExceptions("Начало бронирования раньше, времени окончания бронирования");
        }

        Booking booking = BookingMapper.mapToBooking(bookingDto);
        booking.setBooker(user);
        booking.setItem(item);

        bookingRepository.save(booking);
        log.info("BookingService - в базу добавлена аренда: {} ", booking);

        return BookingMapper.mapToBookingDto(booking);
    }

    // подтверждение или отклонение запроса на бронирование, может быть выполнено только владельцем вещи
    public BookingDto setApprove(Long bookingId, Long userId, Boolean approved) {
        Booking booking = getBookingOrNotFound(bookingId);
        if (booking.getStatus().equals(Status.APPROVED)) {
            throw new ValidatorExceptions("Аренда уже подтверждена!");
        }
        User user = userService.getUserOrNotFound(userId);

        if (booking.getItem().getOwner() != user.getId()) {
            throw new NotFoundException("Пользователь не является владельцем вещи, в изменении статуса отказано");
        }
        if (approved) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }

        bookingRepository.save(booking);
        log.info("BookingService - у аренды: {}, изменён статус - {} ", booking, booking.getStatus());

        return BookingMapper.mapToBookingDto(booking);
    }

    // получение данных о конкретном бронировании для автора бронирования, либо владельца вещи
    public BookingDto findByBookingIdAndUserId(Long bookingId, Long userId) {
        Booking booking = getBookingOrNotFound(bookingId);
        userService.getUserOrNotFound(userId);

        if (booking.getBooker().getId() == userId || booking.getItem().getOwner() == userId) {
            return BookingMapper.mapToBookingDto(booking);
        } else {
            throw new NotFoundException("В доступе отказано, пользователь не имеет отношения к бронированию");
        }
    }

    // получение списка бронирований для пользователя, по статусу
    public Collection<BookingDto> findBookingsByUserIdAndState(Long userId, String state) {
        userService.getUserOrNotFound(userId);

        try {
            Enum.valueOf(State.class, state);
        } catch (IllegalArgumentException e) {
            throw new ValidatorExceptions("Unknown state: UNSUPPORTED_STATUS");
        }

        switch (State.valueOf(state)) {
            case ALL:
                return bookingRepository.findAllByBooker_IdOrderByStartDesc(userId).stream()
                        .map(BookingMapper::mapToBookingDto)
                        .collect(Collectors.toList());
            case CURRENT:
                return bookingRepository.findAllByBooker_IdAndStatusCurrentOrderByStartDesc(userId, LocalDateTime.now()).stream()
                        .map(BookingMapper::mapToBookingDto)
                        .collect(Collectors.toList());
            case FUTURE:
                return bookingRepository.findAllByBooker_IdAndStartIsAfterOrderByStartDesc(userId, LocalDateTime.now()).stream()
                        .map(BookingMapper::mapToBookingDto)
                        .collect(Collectors.toList());
            case PAST:
                return bookingRepository.findAllByBooker_IdAndEndIsBeforeOrderByStartDesc(userId, LocalDateTime.now()).stream()
                        .map(BookingMapper::mapToBookingDto)
                        .collect(Collectors.toList());
            case WAITING:
                return bookingRepository.findAllByBooker_IdAndStatusOrderByStartDesc(userId, Status.WAITING).stream()
                        .map(BookingMapper::mapToBookingDto)
                        .collect(Collectors.toList());
            case REJECTED:
                return bookingRepository.findAllByBooker_IdAndStatusOrderByStartDesc(userId, Status.REJECTED).stream()
                        .map(BookingMapper::mapToBookingDto)
                        .collect(Collectors.toList());
            default:
                return Collections.emptyList();
        }
    }

    // получение списка бронирований владельца вещи, по статусу
    public Collection<BookingDto> findBookingsByOwnerIdAndState(Long ownerId, String state) {
        userService.getUserOrNotFound(ownerId);

        try {
            Enum.valueOf(State.class, state);
        } catch (IllegalArgumentException e) {
            throw new ValidatorExceptions("Unknown state: UNSUPPORTED_STATUS");
        }

        switch (State.valueOf(state)) {
            case ALL:
                return bookingRepository.findAllByItem_OwnerOrderByStartDesc(ownerId).stream()
                        .map(BookingMapper::mapToBookingDto)
                        .collect(Collectors.toList());
            case CURRENT:
                return bookingRepository.findAllByItem_OwnerAndStatusCurrentOrderByStartDesc(ownerId, LocalDateTime.now()).stream()
                        .map(BookingMapper::mapToBookingDto)
                        .collect(Collectors.toList());
            case FUTURE:
                return bookingRepository.findAllByItem_OwnerAndStartIsAfterOrderByStartDesc(ownerId, LocalDateTime.now()).stream()
                        .map(BookingMapper::mapToBookingDto)
                        .collect(Collectors.toList());
            case PAST:
                return bookingRepository.findAllByItem_OwnerAndEndIsBeforeOrderByStartDesc(ownerId, LocalDateTime.now()).stream()
                        .map(BookingMapper::mapToBookingDto)
                        .collect(Collectors.toList());
            case WAITING:
                return bookingRepository.findAllByItem_OwnerAndStatusOrderByStartDesc(ownerId, Status.WAITING).stream()
                        .map(BookingMapper::mapToBookingDto)
                        .collect(Collectors.toList());
            case REJECTED:
                return bookingRepository.findAllByItem_OwnerAndStatusOrderByStartDesc(ownerId, Status.REJECTED).stream()
                        .map(BookingMapper::mapToBookingDto)
                        .collect(Collectors.toList());
            default:
                return Collections.emptyList();
        }
    }

    // получение аренды, если не найдена - ошибка 404
    public Booking getBookingOrNotFound(Long bookingId) {
        return bookingRepository
                .findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Вещь с ИД " + bookingId + " не найден."));
    }
}