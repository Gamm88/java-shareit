package ru.practicum.shareit.request.service;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.request.model.*;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.item.model.item.*;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.service.UserServiceImpl;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.repository.ItemRequestRepository;

import java.util.List;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;
    private final UserServiceImpl userService;

    // добавить новый запрос вещи
    @Override
    public ItemRequestDto addItemRequest(Long userId, ItemRequestDto itemRequestDto) {
        User user = userService.getUserOrNotFound(userId);
        ItemRequest itemRequest = ItemRequestMapper.mapToItemRequest(itemRequestDto);
        itemRequest.setRequestor(user);
        itemRequest.setCreated(LocalDateTime.now());
        itemRequestRepository.save(itemRequest);
        log.info("ItemRequestService - в базу добавлен новый запрос: {} ", itemRequest);

        return ItemRequestMapper.mapToItemRequestDto(itemRequest);
    }

    // получить список своих запросов вместе с данными об ответах на них
    @Override
    public List<ItemRequestDto> getItemRequestsByUser(Long userId) {
        userService.getUserOrNotFound(userId);
        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequestor_IdOrderByCreatedDesc(userId);

        List<ItemRequestDto> itemRequestsDto = ItemRequestMapper.mapToItemRequestDto(itemRequests);
        itemRequestsDto.forEach(this::addResponseToRequest);
        log.info("ItemRequestService - для пользователя с ИД: {}, информация о его запросах: {}", userId, itemRequestsDto);

        return itemRequestsDto;
    }

    // получить список запросов, созданных другими пользователями, результат должен возвращаться постранично
    @Override
    public List<ItemRequestDto> getRequestsOtherUsers(Long userId, int from, int size) {
        userService.getUserOrNotFound(userId);
        PageRequest pageRequest = PageRequest.of(from / size, size);

        List<ItemRequest> itemRequests = itemRequestRepository.
                findAllByRequestor_IdIsNotOrderByCreatedDesc(userId, pageRequest);

        List<ItemRequestDto> itemRequestsDto = ItemRequestMapper.mapToItemRequestDto(itemRequests);
        itemRequestsDto.forEach(this::addResponseToRequest);
        log.info("ItemRequestService - предоставлен список запросов, созданных другими пользователями: {}", itemRequestsDto);

        return itemRequestsDto;
    }

    // получить данные об одном конкретном запросе
    @Override
    public ItemRequestDto getItemRequestByUser(Long userId, Long requestId) {
        userService.getUserOrNotFound(userId);
        ItemRequest itemRequest = getItemRequestOrNotFound(requestId);

        ItemRequestDto itemRequestDto = ItemRequestMapper.mapToItemRequestDto(itemRequest);
        addResponseToRequest(itemRequestDto);
        log.info("ItemRequestService - по ИД запроса: {}, предоставлен запрос: {}", userId, itemRequestDto);

        return itemRequestDto;
    }

    // получение запроса вещи, если не найден - ошибка 404
    @Override
    public ItemRequest getItemRequestOrNotFound(Long ItemRequestId) {
        return itemRequestRepository
                .findById(ItemRequestId)
                .orElseThrow(() -> new NotFoundException("Запрос вещи с ИД " + ItemRequestId + " не найден."));
    }

    // добавление к запросу ответов (предлагаемых вещей) на запрос вещи
    private void addResponseToRequest(ItemRequestDto itemRequestDto) {
        List<Item> items = itemRepository.findAllByRequest_Id(itemRequestDto.getId());
        itemRequestDto.setItems(ItemMapper.mapToItemDto(items));
    }
}