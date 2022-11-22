package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.item.model.comment.CommentDto;
import ru.practicum.shareit.item.model.item.ItemDto;
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

        return itemService.addItem(userId, itemDto);
    }

    // получить все вещи пользователя по ИД пользователя
    @GetMapping
    public Collection<ItemDto> findAllItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("ItemController - получение всех вещей пользователя с ИД: {}", userId);

        return itemService.getItems(userId);
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
    public List<ItemDto> searchByText(@RequestHeader("X-Sharer-User-Id") Long userId,
                                      @RequestParam String text) {
        log.info("ItemController - пользователь с ИД: {} , запросил поиск: [{}]", userId, text);

        return itemService.searchItems(text);
    }

    // добавление комментария к завершённой аренде
    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                 @PathVariable Long itemId,
                                 @Valid @RequestBody CommentDto commentDto) {
        log.info("ItemController - добавление комментария: {}, от пользователь с ИД: {}", commentDto, userId);
        return itemService.addComment(userId, itemId, commentDto);
    }
}