package ru.practicum.shareit.booking;

import lombok.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Booking {
    private long id; // ид бронирования
    private Date start; // дата начала аренды
    private Date end; // дата окончания аренды
    private Boolean confirmed; // подтверждение аренды, подтверждается владельцем, по умолчанию false.
    private Item item; // арендуемая вещь
    private User renter; // арендатор вещи
}
