package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemClient itemClient;

    // создать вещь
    @PostMapping
    public ResponseEntity<Object> addItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @Valid @RequestBody ItemDto itemDto) {
        log.info("ItemController - создание вещи: {}, от пользователя с ИД: {}", itemDto, userId);

        return itemClient.addItem(userId, itemDto);
    }

    // получение всех вещей пользователя по его ИД
    @GetMapping
    public ResponseEntity<Object> getItems(@RequestHeader("X-Sharer-User-Id") Long userId,
                                           @RequestParam(defaultValue = "0") @PositiveOrZero() int from,
                                           @RequestParam(defaultValue = "10") @Positive() int size) {
        log.info("ItemController - получение всех вещей пользователя с ИД: {}", userId);

        return itemClient.getItems(userId, from, size);
    }

    // получить вещь по ИД и пользователю
    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemByItemIdAndUserId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                           @PathVariable Long itemId) {
        log.info("ItemController - получение вещи по ИД: {}", itemId);

        return itemClient.getItemByItemIdAndUserId(itemId, userId);
    }

    // обновление вещи по ИД
    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @PathVariable Long itemId,
                                             @RequestBody ItemDto itemDto) {
        log.info("ItemController - обновление вещи с ИД: {}, новое значение: {}", itemId, itemDto);

        return itemClient.updateItem(userId, itemId, itemDto);
    }

    // удалить вещь по ИД
    @DeleteMapping("/{itemId}")
    public void deleteItemById(@PathVariable("itemId") Long itemId) {
        log.info("ItemController - удаление вещи по ИД: {}", itemId);
        itemClient.deleteItem(itemId);
    }

    // поиск вещей через совпадения текста запроса с наименованием или описанием вещи
    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestHeader("X-Sharer-User-Id") Long userId,
                                              @RequestParam String text) {
        log.info("ItemController - пользователь с ИД: {} , запросил поиск: [{}]", userId, text);

        return itemClient.searchItems(text, userId);
    }

    // добавление комментария к завершённой аренде
    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @PathVariable Long itemId,
                                             @Valid @RequestBody CommentDto commentDto) {
        log.info("ItemController - добавление комментария: {}, от пользователь с ИД: {}", commentDto, userId);
        return itemClient.addComment(userId, itemId, commentDto);
    }
}