package ru.practicum.shareit.request;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/requests")
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    // добавить новый запрос вещи
    @PostMapping
    public ResponseEntity<Object> addItemRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @Valid @RequestBody ItemRequestDto itemRequestDto) {
        log.info("ItemRequestController - создание запроса вещи: {}, от пользователя с ИД: {}", itemRequestDto, userId);

        return itemRequestClient.addItemRequest(userId, itemRequestDto);
    }

    // получить список своих запросов вместе с данными об ответах на них
    @GetMapping
    public ResponseEntity<Object> getItemRequestsByUser(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("ItemRequestController - получение списка запросов вещей, для пользователя с ИД: {}", userId);

        return itemRequestClient.getItemRequestsByUser(userId);
    }

    // получить список запросов, созданных другими пользователями, результат должен возвращаться постранично
    // from — индекс первого элемента, начиная с 0, и size — количество элементов для отображения
    @GetMapping("/all")
    public ResponseEntity<Object> getRequestsOtherUsers(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                      @RequestParam(defaultValue = "0")  @PositiveOrZero() int from,
                                                      @RequestParam(defaultValue = "10") @Positive() int size) {
        log.info("ItemRequestController - получение запросов, для пользователя с ИД: {}, from — {}, size — {}", userId, from, size);

        return itemRequestClient.getRequestsOtherUsers(userId, from, size);
    }

    // получить данные об одном конкретном запросе
    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequestByUser(@RequestHeader("X-Sharer-User-Id") Long userId,
                                     @PathVariable Long requestId) {
        log.info("ItemRequestController - получение данных о запросе с ИД: {}", requestId);

        return itemRequestClient.getItemRequestByUser(userId, requestId);
    }
}