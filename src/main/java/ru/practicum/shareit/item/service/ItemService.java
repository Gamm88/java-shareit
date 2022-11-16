package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemDto;

import java.util.Collection;
import java.util.List;

public interface ItemService {

    // создать вещь
    ItemDto addItem(Long userId, ItemDto itemDto);

    // получить все вещи пользователя по ИД пользователя
    Collection<ItemDto> getItems(Long userId);

    // получить вещь по ИД
    ItemDto getItem(Long itemId);

    // обновление вещи по ИД
    ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto);

    // удалить вещь по ИД
    void deleteItem(Long itemId);

    // поиск вещей через совпадения текста запроса с наименованием или описанием вещи
    List<ItemDto> searchItems(String text);

    // получение вещи, если не найдена - ошибка 404
    Item getItemOrNotFound(Long itemId);
}