package ru.practicum.shareit.booking.model;

import lombok.*;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingDto {
    private Long id; // ид бронирования
    @Future(message = "Дата начала бронирования не может быть раньше текущей даты")
    private LocalDateTime start; // дата начала аренды
    @Future(message = "Дата окончания бронирования не может быть раньше текущей даты")
    private LocalDateTime end; // дата окончания аренды
    @NotNull
    private Long itemId; // ид арендуемой вещи
    private Item item; // арендуемая вещь
    private User booker; // пользователь арендующий вещь
    private Status status; // статус подтверждения аренды

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Item {
        private Long id;
        private String name;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class User {
        private Long id;
    }
}
