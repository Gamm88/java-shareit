package ru.practicum.shareit.item.model.item;

import lombok.*;
import ru.practicum.shareit.item.model.comment.CommentDto;

import java.util.List;
import javax.validation.constraints.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {
    private Long id; // ид вещи

    @NotBlank(message = "Название не может быть пустым")
    @Size(max = 256, message = "Максимальная длина названия — 256 символов")
    private String name; // название

    @NotBlank(message = "Описание не может быть пустым")
    @Size(max = 1024, message = "Максимальная длина описания — 1024 символов")
    private String description; // описание

    @NotNull(message = "Статус доступности аренды должен быть указан")
    private Boolean available; // доступность вещи для аренды, проставлять владелец, по умолчанию true.

    private Long requestId;

    private ItemBooking lastBooking;

    private ItemBooking nextBooking;

    private List<CommentDto> comments;

    @Data
    public static class ItemBooking {
        private final Long id;
        private final Long bookerId;
    }
}