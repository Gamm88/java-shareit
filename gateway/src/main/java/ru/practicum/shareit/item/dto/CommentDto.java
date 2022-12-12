package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {
    private Long id;

    @NotBlank(message = "Описание не может быть пустым")
    @Size(max = 512, message = "Максимальная длина комментария — 500 символов")
    private String text;

    private String authorName;

    @Future(message = "Дата комментария не может быть раньше текущей даты")
    private LocalDateTime created;
}