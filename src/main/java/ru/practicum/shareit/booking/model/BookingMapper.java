package ru.practicum.shareit.booking.model;

import ru.practicum.shareit.item.model.item.Item;
import ru.practicum.shareit.user.model.User;

public class BookingMapper {
    //из BookingDto в booking
    public static Booking mapToBooking(BookingDto bookingDto) {
        return new Booking(
                bookingDto.getId(),
                bookingDto.getStart(),
                bookingDto.getEnd(),
                new Item(0L, 0L, null, null, null),
                new User(),
                bookingDto.getStatus());
    }

    //из booking в BookingDto
    public static BookingDto mapToBookingDto(Booking booking) {
        return new BookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getItem().getId(),
                new BookingDto.Item(
                        booking.getItem().getId(),
                        booking.getItem().getName()
                ),
                new BookingDto.User(
                        booking.getBooker().getId()
                ),
                booking.getStatus());
    }
}