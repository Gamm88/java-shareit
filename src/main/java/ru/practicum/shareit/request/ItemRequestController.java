package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.request.model.*;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/requests")
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    // добавить новый запрос вещи
    @PostMapping
    public ItemRequestDto addItemRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @Valid @RequestBody ItemRequestDto itemRequestDto) {
        log.info("ItemRequestController - создание запроса вещи: {}, от пользователя с ИД: {}", itemRequestDto, userId);

        return itemRequestService.addItemRequest(userId, itemRequestDto);
    }

    // получить список своих запросов вместе с данными об ответах на них
    @GetMapping
    public List<ItemRequestDto> getItemRequestsByUser(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("ItemRequestController - получение списка запросов вещей, для пользователя с ИД: {}", userId);

        return itemRequestService.getItemRequestsByUser(userId);
    }

    // получить список запросов, созданных другими пользователями, результат должен возвращаться постранично
    // from — индекс первого элемента, начиная с 0, и size — количество элементов для отображения
    @GetMapping("/all")
    public List<ItemRequestDto> getRequestsOtherUsers(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                      @RequestParam(defaultValue = "0")  @PositiveOrZero() int from,
                                                      @RequestParam(defaultValue = "10") @Positive() int size) {
        log.info("ItemRequestController - получение запросов, для пользователя с ИД: {}, from — {}, size — {}", userId, from, size);

        return itemRequestService.getRequestsOtherUsers(userId, from, size);
    }

    // получить данные об одном конкретном запросе
    @GetMapping("/{requestId}")
    public ItemRequestDto getRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                     @PathVariable Long requestId) {
        log.info("ItemRequestController - получение данных о запросе с ИД: {}", requestId);

        return itemRequestService.getItemRequestByUser(userId, requestId);
    }
}