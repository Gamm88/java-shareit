package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.model.ItemDto;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;

    // создать вещь
    public ItemDto createItem(Long userId, ItemDto itemDto) {
        userService.getUserOrNotFound(userId);
        Item createdItem = itemRepository.createItem(ItemMapper.toItem(userId, itemDto));
        log.info("ItemService - в базу добавлена вещь: {} ", createdItem);

        return ItemMapper.toItemDto(createdItem);
    }

    // получить все вещи пользователя по ИД пользователя
    public Collection<ItemDto> getAllItems(Long userId) {
        Collection<ItemDto> itemsDtoList = itemRepository.getAllItems().stream()
                .filter(Item -> Item.getOwnerId() == userId)
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
        log.info("ItemService - для пользователя с ИД: {} предоставлен список вещей: {} ", userId, itemsDtoList);

        return itemsDtoList;
    }

    // получить вещь по ИД
    public ItemDto getItemById(Long itemId) {
        Item getItem = getItemOrNotFound(itemId);
        ItemDto itemDto = ItemMapper.toItemDto(getItem);
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

        updatedItem = itemRepository.updateItem(updatedItem);
        log.info("ItemService - в базе обновлена вещь: {}", updatedItem);

        return ItemMapper.toItemDto(updatedItem);
    }

    // удалить все вещи
    public void deleteAllItems() {
        log.info("ItemService - удаление всех пользователей");
        itemRepository.deleteAllItems();
    }

    // удалить вещь по ИД
    public void deleteItemById(Long itemId) {
        getItemOrNotFound(itemId);
        log.info("ItemController - удаление пользователя по ИД: {}", itemId);
        itemRepository.deleteItemById(itemId);
    }

    // поиск вещей через совпадения текста запроса с наименованием или описанием вещи
    public Collection<ItemDto> searchItems(String text) {
        Collection<ItemDto> itemsDtoList = new ArrayList<>();
        if (text.equals("")) {
            log.info("ItemService - запрос не содержит значений");

            return itemsDtoList;
        }
        itemsDtoList = itemRepository.getAllItems().stream()
                .filter(Item::getAvailable)
                .filter(Item -> Item.getName().toLowerCase().contains(text.toLowerCase())
                        || Item.getDescription().toLowerCase().contains(text.toLowerCase()))
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
        log.info("ItemService - по запросу: {} предоставлен список вещей: {} ", text, itemsDtoList);

        return itemsDtoList;
    }

    // получение вещи, если не найдена - ошибка 404
    public Item getItemOrNotFound(Long itemId) {
        Item item = itemRepository.getItemById(itemId);
        if (item == null) {
            throw new NotFoundException("Вещь с ИД: " + itemId + " не найден.");
        }

        return item;
    }
}