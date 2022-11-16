package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.item.model.Item;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.model.ItemDto;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.user.servise.UserServiceImpl;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.List;
import java.util.ArrayList;
import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserServiceImpl userService;

    // создать вещь
    public ItemDto addItem(Long userId, ItemDto itemDto) {
        userService.getUserOrNotFound(userId);
        Item newItem = itemRepository.save(ItemMapper.mapToItem(itemDto, userId));
        log.info("ItemService - в базу добавлена вещь: {} ", newItem);

        return ItemMapper.mapToItemDto(newItem);
    }

    // получить все вещи пользователя по ИД пользователя
    public Collection<ItemDto> getItems(Long userId) {
        Collection<ItemDto> itemsDtoList = ItemMapper.mapToItemDto(itemRepository.findAllByOwnerId(userId));
        log.info("ItemService - для пользователя с ИД: {} предоставлен список вещей: {} ", userId, itemsDtoList);

        return itemsDtoList;
    }

    // получить вещь по ИД
    public ItemDto getItem(Long itemId) {
        Item getItem = getItemOrNotFound(itemId);
        ItemDto itemDto = ItemMapper.mapToItemDto(getItem);
        log.info("ItemService - по ИД: {} получена вещь: {}", itemId, itemDto);

        return itemDto;
    }

    // обновление вещи по ИД
    public ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto) {
        Item updatedItem = getItemOrNotFound(itemId);

        if (updatedItem.getOwnerId() != userId) {
            throw new NotFoundException("Вещь с ИД: " + itemId + " у пользователя с ИД: " + userId + " не найден.");
        }

        String newName = itemDto.getName();
        String newDescription = itemDto.getDescription();
        Boolean newAvailable = itemDto.getAvailable();

        if (newName != null) {
            updatedItem.setName(newName);
        }
        if (newDescription != null) {
            updatedItem.setDescription(newDescription);
        }
        if (newAvailable != null) {
            updatedItem.setAvailable(newAvailable);
        }

        updatedItem = itemRepository.save(updatedItem);
        log.info("ItemService - в базе обновлена вещь: {}", updatedItem);

        return ItemMapper.mapToItemDto(updatedItem);
    }

    // удалить вещь по ИД
    public void deleteItem(Long itemId) {
        getItemOrNotFound(itemId);
        log.info("ItemController - удаление пользователя по ИД: {}", itemId);
        itemRepository.deleteById(itemId);
    }

    // поиск вещей через совпадения текста запроса с наименованием или описанием вещи
    public List<ItemDto> searchItems(String text) {
        List<ItemDto> itemsDtoList = new ArrayList<>();
        if (text.equals("")) {
            log.info("ItemService - запрос не содержит значений");

            return itemsDtoList;
        }
        itemsDtoList = ItemMapper.mapToItemDto(itemRepository.searchByText(text));
        log.info("ItemService - по запросу: {} предоставлен список вещей: {} ", text, itemsDtoList);

        return itemsDtoList;
    }

    // получение вещи, если не найдена - ошибка 404
    public Item getItemOrNotFound(Long itemId) {
        return itemRepository
                .findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с ИД " + itemId + " не найден."));
    }
}