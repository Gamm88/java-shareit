package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class ItemRepository {
    private long id = 0; // уникальный ID вещи
    private final Map<Long, Item> items = new HashMap<>(); // хранение вещей

    // создать вещь
    public Item createItem(Item item) {
        item.setId(++id);
        items.put(item.getId(), item);

        return item;
    }

    // получить все вещи
    public Collection<Item> getAllItems() {

        return items.values();
    }

    // получить вещь по ИД
    public Item getItemById(Long itemId) {

        return items.get(itemId);
    }

    // обновление вещи по ИД
    public Item updateItem(Item item) {
        items.put(item.getId(), item);

        return items.get(item.getId());
    }

    // удалить вещь по ИД
    public void deleteItemById(Long itemId) {
        items.remove(itemId);
    }
}
