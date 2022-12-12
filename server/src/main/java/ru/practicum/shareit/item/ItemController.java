package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.model.item.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.model.comment.CommentDto;
import org.springframework.validation.annotation.Validated;

import java.util.Collection;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    // создать вещь
    @PostMapping
    public ItemDto addItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                           @RequestBody ItemDto itemDto) {
        log.info("ItemController - создание вещи: {}, от пользователя с ИД: {}", itemDto, userId);

        return itemService.addItem(userId, itemDto);
    }

    // получение всех вещей пользователя по его ИД
    @GetMapping
    public Collection<ItemDto> getItems(@RequestHeader("X-Sharer-User-Id") Long userId,
                                        @RequestParam(defaultValue = "0") int from,
                                        @RequestParam(defaultValue = "10") int size) {
        log.info("ItemController - получение всех вещей пользователя с ИД: {}", userId);

        return itemService.getItems(userId, from, size);
    }

    // получить вещь по ИД и пользователю
    @GetMapping("/{itemId}")
    public ItemDto getItemByItemIdAndUserId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                            @PathVariable Long itemId) {
        log.info("ItemController - получение вещи по ИД: {}", itemId);

        return itemService.getItemByItemIdAndUserId(itemId, userId);
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
        itemService.deleteItem(itemId);
    }

    // поиск вещей через совпадения текста запроса с наименованием или описанием вещи
    @GetMapping("/search")
    public Collection<ItemDto> searchItems(@RequestHeader("X-Sharer-User-Id") Long userId,
                                           @RequestParam(defaultValue = "0") int from,
                                           @RequestParam(defaultValue = "10") int size,
                                           @RequestParam String text) {
        log.info("ItemController - пользователь с ИД: {} , запросил поиск: [{}]", userId, text);

        return itemService.searchItems(text, from, size);
    }

    // добавление комментария к завершённой аренде
    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                 @PathVariable Long itemId,
                                 @RequestBody CommentDto commentDto) {
        log.info("ItemController - добавление комментария: {}, от пользователь с ИД: {}", commentDto, userId);
        return itemService.addComment(userId, itemId, commentDto);
    }
}