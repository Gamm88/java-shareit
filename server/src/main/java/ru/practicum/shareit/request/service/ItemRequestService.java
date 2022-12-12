package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    // добавить новый запрос вещи
    ItemRequestDto addItemRequest(Long userId, ItemRequestDto itemRequestDto);

    // получить список своих запросов вместе с данными об ответах на них
    List<ItemRequestDto> getItemRequestsByUser(Long userId);

    // получить список запросов, созданных другими пользователями, результат должен возвращаться постранично
    List<ItemRequestDto> getRequestsOtherUsers(Long userId, int from, int size);

    // получить данные об одном конкретном запросе
    ItemRequestDto getItemRequestByUser(Long userId, Long requestId);

    // получение запроса вещи, если не найден - ошибка 404
    ItemRequest getItemRequestOrNotFound(Long itemRequestId);
}