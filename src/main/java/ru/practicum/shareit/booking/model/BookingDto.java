package ru.practicum.shareit.booking.model;

import lombok.*;

import javax.validation.constraints.Future;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingDto {
    private long id; // ид бронирования
    @Future(message = "Дата начала бронирования не может быть раньше текущей даты")
    private Date startDate; // дата начала аренды
    @Future(message = "Дата окончания бронирования не может быть раньше текущей даты")
    private Date endDate; // дата окончания аренды
    private long itemId; // ид арендуемой вещи
    private Status status; // статус подтверждения аренды
    private Item item; // арендуемая вещь
    private User booker; // пользователь арендующий вещь

    @Data
    public static class Item {
        private final long id;
        private final String name;
    }

    @Data
    public static class User {
        private final long id;
    }
}
