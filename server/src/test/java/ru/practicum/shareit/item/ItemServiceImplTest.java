package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidatorExceptions;
import ru.practicum.shareit.item.model.comment.Comment;
import ru.practicum.shareit.item.model.comment.CommentMapper;
import ru.practicum.shareit.item.model.item.Item;
import ru.practicum.shareit.item.model.item.ItemDto;
import ru.practicum.shareit.item.model.item.ItemMapper;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
@ExtendWith(MockitoExtension.class)
public class ItemServiceImplTest {

    @Autowired
    private final EntityManager em;

    @Autowired
    private final ItemService itemService;

    @MockBean
    private ItemRepository itemRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private CommentRepository commentRepository;

    @MockBean
    private BookingRepository bookingRepository;

    @MockBean
    private ItemRequestRepository itemRequestRepository;

    private User user = new User().toBuilder()
            .id(1L)
            .name("Mike")
            .email("mike@mail.ru")
            .build();

    private ItemRequest itemRequest = new ItemRequest().toBuilder()
            .id(1L)
            .description("testDecr")
            .requestor(user)
            .created(LocalDateTime.now())
            .build();

    private Item item = new Item().toBuilder()
            .id(1L)
            .available(true)
            .owner(1L)
            .name("Дрель")
            .description("Новая")
            .request(itemRequest)
            .build();

    private Comment comment = new Comment().toBuilder()
            .id(1L)
            .text("text")
            .item(item)
            .author(user)
            .created(LocalDateTime.now()).build();

    private Booking booking = new Booking().toBuilder()
            .id(1L)
            .item(item)
            .booker(user)
            .status(Status.APPROVED)
            .build();

    @Test
    void testAddComment() {
        booking.setStart(LocalDateTime.of(2000, 11, 11, 11, 11));
        booking.setEnd(LocalDateTime.of(2000, 11, 11, 11, 12));

        Mockito
                .when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        Mockito
                .when(bookingRepository.findByBooker_Id(1L))
                .thenReturn(List.of(booking));
        Mockito
                .when(commentRepository.save(any(Comment.class)))
                .thenReturn(comment);

        Throwable throwableWithoutBookingUser = assertThrows(ValidatorExceptions.class,
                () -> itemService.addComment(2L, 1L, CommentMapper.mapToCommentDto(comment)));
        assertNotNull(throwableWithoutBookingUser.getMessage());

        Throwable throwableUserId = assertThrows(ValidatorExceptions.class,
                () -> itemService.addComment(0L, 1L, CommentMapper.mapToCommentDto(comment)));
        assertNotNull(throwableUserId.getMessage());

        comment.setText("text");

        itemService.addComment(1L, 1L, CommentMapper.mapToCommentDto(comment));

        verify(itemRepository, times(3)).findById(anyLong());
        verify(userRepository, times(3)).findById(anyLong());
        verify(bookingRepository, times(3)).findByBooker_Id(anyLong());
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    void testGetItemOrNotFound() {
        Mockito
                .when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));
        ItemDto targetItem = ItemMapper.mapToItemDto(itemService.getItemOrNotFound(1L));

        Assertions.assertEquals(targetItem.getName(), item.getName());
        Assertions.assertEquals(targetItem.getDescription(), item.getDescription());
        Assertions.assertEquals(targetItem.getId(), item.getId());

        verify(itemRepository, times(1)).findById(anyLong());
    }

    @Test
    void testGetItemByItemIdAndUserId() {
        booking.setStart(LocalDateTime.of(2000, 11, 11, 11, 11));
        booking.setEnd(LocalDateTime.of(2000, 11, 11, 11, 12));

        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        Mockito
                .when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));

        Mockito
                .when(itemRepository.findById(anyLong()))
                .thenAnswer(invocationOnMock -> {
                    long itemId = invocationOnMock.getArgument(0, Long.class);
                    if (itemId <= 0) {
                        throw new NotFoundException("Такой вещи не существует");
                    } else {
                        return Optional.ofNullable(item);
                    }
                });

        ItemDto targetItem = itemService.getItemByItemIdAndUserId(1L, 1L);

        Assertions.assertEquals(targetItem.getName(), item.getName());
        Assertions.assertEquals(targetItem.getDescription(), item.getDescription());
        Assertions.assertEquals(targetItem.getId(), item.getId());
    }

    @Test
    void getAllItemsByUserId() {
        item.setId(1L);
        item.setOwner(1L);
        List<Item> sourceList = List.of(item);
        List<ItemDto> sourceDtoList = ItemMapper.mapToItemDto(sourceList);

        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(itemRepository.findAllByOwnerOrderByIdAsc(anyLong(), any()))
                .thenReturn(sourceList);

        List<ItemDto> targetList = (List<ItemDto>) itemService.getItems(user.getId(), 0, 10);

        verify(itemRepository, times(1)).findAllByOwnerOrderByIdAsc(anyLong(), any());

        assertThat(targetList, hasSize(1));

        for (ItemDto itemDto : sourceDtoList) {
            assertThat(targetList, hasItem(allOf(
                    hasProperty("name", equalTo(itemDto.getName())),
                    hasProperty("description", equalTo(itemDto.getDescription()))
            )));
        }
    }

    @Test
    void testAddItem() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(itemRequestRepository.findById(anyLong()))
                .thenReturn(Optional.of(itemRequest));

        itemService.addItem(1L, ItemMapper.mapToItemDto(item));

        verify(itemRepository, times(1)).save(any());
    }

    @Test
    void updateItem() {
        item.setId(1L);
        item.setOwner(1L);
        List<Item> sourceList = List.of(item);

        Item itemOther = new Item().toBuilder()
                .id(2L)
                .available(true)
                .owner(2L)
                .name("Пила")
                .description("Ручная")
                .request(new ItemRequest().toBuilder()
                        .id(1L)
                        .description("testDecr")
                        .requestor(user)
                        .created(LocalDateTime.now())
                        .build())
                .build();

        Mockito
                .when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));

        Mockito
                .when(itemRepository.findAll())
                .thenReturn(sourceList);

        Mockito.when(itemRepository.save(any(Item.class)))
                .thenReturn(itemOther);

        itemService.updateItem(1L, 1L, ItemMapper.mapToItemDto(itemOther));

        verify(itemRepository, times(1)).save(any(Item.class));
        verify(itemRepository, times(1)).findById(anyLong());
    }

    @Test
    void searchItemsByText() {
        List<Item> sourceList = List.of(item);

        Mockito
                .when(itemRepository.searchByText(anyString(), any()))
                .thenReturn(sourceList);

        List<ItemDto> targetList = (List<ItemDto>) itemService.searchItems("Дрель", 0, 10);

        for (Item sourceItem : sourceList) {
            assertThat(targetList, hasItem(allOf(
                    hasProperty("name", equalTo(sourceItem.getName())),
                    hasProperty("description", equalTo(sourceItem.getDescription()))
            )));
        }

        verify(itemRepository, times(1)).searchByText(anyString(), any());
    }
}