package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Item {
    @NotNull
    private long id; // ид вещи
    @NotNull
    private long ownerId; // ид владельца вещи
    @NotBlank(message = "Название не может быть пустым")
    @Size(max = 100, message = "Максимальная длина названия — 100 символов")
    private String name; // название
    @NotBlank(message = "Описание не может быть пустым")
    @Size(max = 500, message = "Максимальная длина описания — 500 символов")
    private String description; // описание
    @NotNull(message = "Статус доступности аренды должен быть указан")
    private Boolean available; // доступность вещи для аренды, проставлять владелец, по умолчанию true.
}