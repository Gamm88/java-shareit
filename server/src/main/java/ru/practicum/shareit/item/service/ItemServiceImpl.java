package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exception.ValidatorExceptions;
import ru.practicum.shareit.item.model.comment.Comment;
import ru.practicum.shareit.item.model.comment.CommentDto;
import ru.practicum.shareit.item.model.comment.CommentMapper;
import ru.practicum.shareit.item.model.item.Item;
import ru.practicum.shareit.item.model.item.ItemDto;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.model.item.ItemMapper;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.*;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRequestService itemRequestService;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRepository itemRepository;
    private final UserService userService;

    // создать вещь
    @Override
    public ItemDto addItem(Long userId, ItemDto itemDto) {
        userService.getUserOrNotFound(userId);
        Item item = ItemMapper.mapToItem(itemDto, userId);

        if (itemDto.getRequestId() != null) {
            item.setRequest(itemRequestService.getItemRequestOrNotFound(itemDto.getRequestId()));
        }
        itemRepository.save(item);
        log.info("ItemService - в базу добавлена вещь: {} ", item);

        return ItemMapper.mapToItemDto(item);
    }

    // получить все вещи пользователя по ИД пользователя
    @Override
    public Collection<ItemDto> getItems(Long userId, int from, int size) {
        userService.getUserOrNotFound(userId);
        PageRequest pageRequest = PageRequest.of(from / size, size);

        Collection<ItemDto> itemsDtos = ItemMapper.mapToItemDto(
                itemRepository.findAllByOwnerOrderByIdAsc(userId, pageRequest));

        for (ItemDto itemDto : itemsDtos) {
            addNextBooking(itemDto);
            addLastBooking(itemDto);
            addComments(itemDto);
        }
        log.info("ItemService - для пользователя с ИД: {} предоставлен список вещей: {} ", userId, itemsDtos);

        return itemsDtos;
    }

    // получить вещь по ИД вещи и ИД пользователя
    @Override
    public ItemDto getItemByItemIdAndUserId(Long itemId, Long userId) {
        userService.getUserOrNotFound(userId);
        Item getItem = getItemOrNotFound(itemId);
        ItemDto itemDto = ItemMapper.mapToItemDto(getItem);

        // если запрос поступил от владельца вещи, то добавляем информацию о последней и ближайшей аренде
        if (getItem.getOwner().equals(userId)) {
            addNextBooking(itemDto);
            addLastBooking(itemDto);
        }
        addComments(itemDto);
        log.info("ItemService - для пользователя с ИД: {}, найдена вещь: {}", itemId, itemDto);

        return itemDto;
    }

    // обновление вещи по ИД
    @Override
    public ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto) {
        Item updatedItem = getItemOrNotFound(itemId);

        if (!updatedItem.getOwner().equals(userId)) {
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
    @Override
    public void deleteItem(Long itemId) {
        getItemOrNotFound(itemId);
        log.info("ItemController - удаление пользователя по ИД: {}", itemId);
        itemRepository.deleteById(itemId);
    }

    // поиск вещей через совпадения текста запроса с наименованием или описанием вещи
    @Override
    public Collection<ItemDto> searchItems(String text, int from, int size) {
        List<ItemDto> itemsDtoList = new ArrayList<>();
        PageRequest pageRequest = PageRequest.of(from / size, size);

        if (text.equals("")) {
            log.info("ItemService - запрос не содержит значений");

            return itemsDtoList;
        }
        itemsDtoList = ItemMapper.mapToItemDto(itemRepository.searchByText(text, pageRequest));
        log.info("ItemService - по запросу: {} предоставлен список вещей: {} ", text, itemsDtoList);

        return itemsDtoList;
    }

    // добавление комментария к завершённой аренде
    @Override
    public CommentDto addComment(Long userId, Long itemId, CommentDto commentDto) {
        Item item = getItemOrNotFound(itemId);
        User user = userService.getUserOrNotFound(userId);
        Collection<Booking> bookings = bookingRepository.findByBooker_Id(userId);

        if (bookings.stream()
                .anyMatch(booking -> booking.getItem().getId().equals(item.getId())
                        && booking.getStatus().equals(Status.APPROVED)
                        && booking.getEnd().isBefore(LocalDateTime.now()))) {
            commentDto.setAuthorName(user.getName());
            commentDto.setCreated(LocalDateTime.now());
        } else {
            throw new ValidatorExceptions("Вещь не была в аренде или аренда ещё не завершена");
        }
        Comment comment = commentRepository.save(CommentMapper.mapToComment(commentDto, item, user));
        log.info("ItemService - в базу добавлен комментарий: {} ", comment);

        return CommentMapper.mapToCommentDto(comment);
    }

    // получение вещи, если не найдена - ошибка 404
    @Override
    public Item getItemOrNotFound(Long itemId) {
        return itemRepository
                .findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с ИД " + itemId + " не найден."));
    }

    // добавление ближайшей аренды вещи
    private void addNextBooking(ItemDto itemDto) {
        Optional<Booking> booking = bookingRepository
                .findTop1BookingByItem_IdAndEndIsAfterAndStatusIsOrderByEndAsc(
                        itemDto.getId(), LocalDateTime.now(), Status.APPROVED);
        if (booking.isPresent()) {
            itemDto.setNextBooking(new ItemDto.ItemBooking(booking.get().getId(), booking.get().getBooker().getId()));
        } else {
            itemDto.setNextBooking(null);
        }
    }

    // добавление последней аренды вещи
    private void addLastBooking(ItemDto itemDto) {
        Optional<Booking> booking = bookingRepository
                .findTop1BookingByItem_IdAndEndIsBeforeAndStatusIsOrderByEndDesc(
                        itemDto.getId(), LocalDateTime.now(), Status.APPROVED);
        if (booking.isPresent()) {
            itemDto.setLastBooking(new ItemDto.ItemBooking(booking.get().getId(), booking.get().getBooker().getId()));
        } else {
            itemDto.setLastBooking(null);
        }
    }

    // добавление комментариев для вещи
    private void addComments(ItemDto itemDto) {
        itemDto.setComments(CommentMapper.mapToItemDto(commentRepository.findAllByItem_Id(itemDto.getId())));
    }
}