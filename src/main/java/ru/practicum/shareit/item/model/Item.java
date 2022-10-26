package ru.practicum.shareit.item.model;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Item {
    private long id; // ид вещи
    private long ownerId; // ид владельца вещи
    private String name; // название
    private String description; // описание
    private Boolean available; // доступность вещи для аренды, проставлять владелец, по умолчанию true.
}