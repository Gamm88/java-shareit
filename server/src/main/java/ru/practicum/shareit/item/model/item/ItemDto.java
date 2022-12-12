package ru.practicum.shareit.item.model.item;

import lombok.*;
import ru.practicum.shareit.item.model.comment.CommentDto;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {
    private Long id; // ид вещи
    private String name; // название
    private String description; // описание
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