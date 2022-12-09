package ru.practicum.shareit.request.dto;

import lombok.*;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequestDto {
    private Long id;

    @NotBlank(message = "Описание не может быть пустым")
    @Size(max = 1024, message = "Максимальная длина описания — 1024 символов")
    private String description;

    private Long requestorId;

    private LocalDateTime created;

    private List<ItemDto> items;
}
