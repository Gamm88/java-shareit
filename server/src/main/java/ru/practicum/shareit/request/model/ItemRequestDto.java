package ru.practicum.shareit.request.model;

import lombok.*;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.item.model.item.ItemDto;

import java.util.List;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequestDto {
    private Long id;
    private String description;
    private User requestor;
    private LocalDateTime created;
    private List<ItemDto> items;
}
