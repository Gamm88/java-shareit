package ru.practicum.shareit.request.model;

import java.util.List;
import java.util.ArrayList;

public class ItemRequestMapper {
    //из ItemRequestDto в ItemRequest
    public static ItemRequest mapToItemRequest(ItemRequestDto itemRequestDto) {
        return new ItemRequest(
                itemRequestDto.getId(),
                itemRequestDto.getDescription(),
                itemRequestDto.getRequestor(),
                itemRequestDto.getCreated());
    }

    //из ItemRequest в ItemRequestDto
    public static ItemRequestDto mapToItemRequestDto(ItemRequest itemRequest) {
        return new ItemRequestDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getRequestor(),
                itemRequest.getCreated(),
                null);
    }

    //получение списка ItemRequestDto из списка ItemRequest
    public static List<ItemRequestDto> mapToItemRequestDto(Iterable<ItemRequest> itemRequests) {
        List<ItemRequestDto> dtos = new ArrayList<>();
        for (ItemRequest itemRequest : itemRequests) {
            dtos.add(mapToItemRequestDto(itemRequest));
        }
        return dtos;
    }
}
