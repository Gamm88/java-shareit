package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exception.ValidatorExceptions;
import ru.practicum.shareit.item.model.comment.Comment;
import ru.practicum.shareit.item.model.comment.CommentDto;
import ru.practicum.shareit.item.model.comment.CommentMapper;
import ru.practicum.shareit.item.model.item.Item;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.model.item.ItemDto;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.model.item.ItemMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.servise.UserServiceImpl;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final UserServiceImpl userService;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    // создать вещь
    public ItemDto addItem(Long userId, ItemDto itemDto) {
        userService.getUserOrNotFound(userId);
        Item newItem = itemRepository.save(ItemMapper.mapToItem(itemDto, userId));
        log.info("ItemService - в базу добавлена вещь: {} ", newItem);

        return ItemMapper.mapToItemDto(newItem);
    }

    /**
     * TODO разобраться, возможен ли рефакторинг для более понятного кода1
     */
    // получить все вещи пользователя по ИД пользователя
    public Collection<ItemDto> getItems(Long userId) {
        Collection<ItemDto> itemsDtos = ItemMapper.mapToItemDto(itemRepository.findAllByOwner(userId));

        for (ItemDto itemDto : itemsDtos) {
            Collection<Booking> bookingList = bookingRepository.findByItem_Id(itemDto.getId());
            Booking nextBooking = bookingList.stream().filter(booking -> booking.getStart().isAfter(LocalDateTime.now()))
                    .min((Comparator.comparing(Booking::getStart))).orElse(null);
            Booking lastBooking = bookingList.stream().filter(booking -> booking.getEnd().isBefore(LocalDateTime.now()))
                    .max((Comparator.comparing(Booking::getEnd))).orElse(null);
            if (nextBooking != null) {
                itemDto.setNextBooking(new ItemDto.ItemBooking(nextBooking.getId(), nextBooking.getBooker().getId()));
            }
            if (lastBooking != null) {
                itemDto.setLastBooking(new ItemDto.ItemBooking(lastBooking.getId(), lastBooking.getBooker().getId()));
            }
        }
        itemsDtos.forEach(itemDto -> itemDto.setComments(addCommentForItem(itemDto)));

        log.info("ItemService - для пользователя с ИД: {} предоставлен список вещей: {} ", userId, itemsDtos);

        return itemsDtos;
    }

    /**
     * TODO разобраться, возможен ли рефакторинг для более понятного кода1
     */
    // получить вещь по ИД и пользователю
    public ItemDto getItemByItemIdAndUserId(Long itemId, Long userId) {
        userService.getUserOrNotFound(userId);
        Item getItem = getItemOrNotFound(itemId);
        ItemDto itemDto = ItemMapper.mapToItemDto(getItem);
        Collection<Booking> bookings = bookingRepository.findByItem_IdAndItem_Owner(itemId, userId);

        if (!bookings.isEmpty()) {
            Booking nextBooking = bookings.stream().filter(booking -> booking.getStart().isAfter(LocalDateTime.now()))
                    .min((Comparator.comparing(Booking::getStart))).orElse(null);
            Booking lastBooking = bookings.stream().filter(booking -> booking.getEnd().isBefore(LocalDateTime.now()))
                    .max((Comparator.comparing(Booking::getEnd))).orElse(null);
            if (nextBooking != null) {
                itemDto.setNextBooking(new ItemDto.ItemBooking(nextBooking.getId(), nextBooking.getBooker().getId()));
            }
            if (lastBooking != null) {
                itemDto.setLastBooking(new ItemDto.ItemBooking(lastBooking.getId(), lastBooking.getBooker().getId()));
            }
        }
        itemDto.setComments(addCommentForItem(itemDto));

        log.info("ItemService - по ИД: {} получена вещь: {}", itemId, itemDto);

        return itemDto;
    }

    // обновление вещи по ИД
    public ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto) {
        Item updatedItem = getItemOrNotFound(itemId);

        if (updatedItem.getOwner() != userId) {
            throw new NotFoundException("Вещь с ИД: " + itemId + " у пользователя с ИД: " + userId + " не найден.");
        }

        String newName = itemDto.getName();
        Boolean newAvailable = itemDto.getAvailable();
        String newDescription = itemDto.getDescription();

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

    // добавление комментария к завершённой аренде
    public CommentDto addComment(Long userId, Long itemId, CommentDto commentDto) {
        Item item = getItemOrNotFound(itemId);
        User user = userService.getUserOrNotFound(userId);
        Collection<Booking> bookings = bookingRepository.findByBooker_Id(userId);

        if (bookings.stream()
                .anyMatch(booking -> booking.getItem().getId() == item.getId()
                        && booking.getStatus().equals(Status.APPROVED)
                        && booking.getEnd().isBefore(LocalDateTime.now()))) {
            commentDto.setAuthorName(user.getName());
            commentDto.setCreated(LocalDateTime.now());
        } else {
            throw new ValidatorExceptions("Вещь небыла в аренде или аренда ещё не завершена");
        }
        Comment comment = commentRepository.save(CommentMapper.mapToComment(commentDto, item, user));
        log.info("ItemService - в базу добавлен комментарий: {} ", comment);

        return CommentMapper.mapToCommentDto(comment);
    }

    // получение вещи, если не найдена - ошибка 404
    public Item getItemOrNotFound(Long itemId) {
        return itemRepository
                .findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с ИД " + itemId + " не найден."));
    }

    // добавление комментариев для вещи
    private List<CommentDto> addCommentForItem(ItemDto itemDto) {
        return CommentMapper.mapToItemDto(commentRepository.findAllByItem_Id(itemDto.getId()));
    }
}