package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.item.model.ItemDto;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.service.ItemServiceImpl;

import java.util.Collection;
import java.util.List;
import javax.validation.Valid;

/**
 * TODO разобраться как перехватить ошибку если @RequestHeader не заполнен или отсутствует.
 */

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemServiceImpl itemService;

    // создать вещь
    @PostMapping
    public ItemDto createItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @Valid @RequestBody ItemDto itemDto) {
        log.info("ItemController - создание вещи: {}", itemDto);

        return itemService.createItem(userId, itemDto);
    }

    // получить все вещи пользователя по ИД пользователя
    @GetMapping
    public Collection<ItemDto> findAllItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("ItemController - получение всех пользователей");

        return itemService.getAllItems(userId);
    }

    // получить вещь по ИД
    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable Long itemId) {
        log.info("ItemController - получение вещи по ИД: {}", itemId);

        return itemService.getItemById(itemId);
    }

    // обновление вещи по ИД
    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @PathVariable Long itemId,
                              @RequestBody ItemDto itemDto) {
        log.info("ItemController - обновление вещи с ИД: {}, новое значение: {}", itemId, itemDto);

        return itemService.updateItem(userId, itemId, itemDto);
    }

    // удалить вещь по ИД
    @DeleteMapping("/{itemId}")
    public void deleteItemById(@PathVariable("itemId") Long itemId) {
        log.info("ItemController - удаление вещи по ИД: {}", itemId);
        itemService.deleteItemById(itemId);
    }

    // поиск вещей через совпадения текста запроса с наименованием или описанием вещи
    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestHeader("X-Sharer-User-Id") Long userId,
                                     @RequestParam String text) {
        log.info("ItemController - пользователь с ИД: {} , запросил поиск: [{}]", userId, text);

        return itemService.searchItems(text);
    }
}
