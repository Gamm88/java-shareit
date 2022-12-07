package ru.practicum.shareit.request.model;

import lombok.*;
import ru.practicum.shareit.item.model.item.ItemDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.time.LocalDateTime;
import javax.validation.constraints.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequestDto {
    private Long id;

    @NotBlank(message = "Описание не может быть пустым")
    @Size(max = 1024, message = "Максимальная длина описания — 1024 символов")
    private String description;

    private User requestor;

    private LocalDateTime created;

    private List<ItemDto> items;
}
