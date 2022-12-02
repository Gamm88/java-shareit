package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.item.Item;
import ru.practicum.shareit.item.model.item.ItemDto;
import ru.practicum.shareit.item.model.comment.CommentDto;

import java.util.Collection;

public interface ItemService {
    // создать вещь
    ItemDto addItem(Long userId, ItemDto itemDto);

    // получить все вещи пользователя по ИД пользователя
    Collection<ItemDto> getItems(Long userId, int from, int size);

    // получить вещь по ИД и пользователю
    ItemDto getItemByItemIdAndUserId(Long itemId, Long userId);

    // обновление вещи по ИД
    ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto);

    // удалить вещь по ИД
    void deleteItem(Long itemId);

    // поиск вещей через совпадения текста запроса с наименованием или описанием вещи
    Collection<ItemDto> searchItems(String text, int from, int size);

    // добавление комментария к завершённой аренде
    CommentDto addComment(Long userId, Long itemId, CommentDto commentDto);

    // получение вещи, если не найдена - ошибка 404
    Item getItemOrNotFound(Long itemId);
}