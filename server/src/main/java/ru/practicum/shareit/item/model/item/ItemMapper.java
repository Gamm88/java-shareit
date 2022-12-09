package ru.practicum.shareit.item.model.item;

import java.util.ArrayList;
import java.util.List;

public class ItemMapper {

    //из ItemDto в Item
    public static Item mapToItem(ItemDto itemDto, Long userId) {
        return new Item(
                itemDto.getId(),
                userId,
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                null);
    }

    //из Item в ItemDto
    public static ItemDto mapToItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequest() != null ? item.getRequest().getId() : 0,
                null,
                null,
                new ArrayList<>());
    }

    //получение списка ItemDto из списка Item
    public static List<ItemDto> mapToItemDto(Iterable<Item> items) {
        List<ItemDto> dtos = new ArrayList<>();
        for (Item item : items) {
            dtos.add(mapToItemDto(item));
        }
        return dtos;
    }
}