package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.model.BookingDto;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.item.model.comment.CommentDto;
import ru.practicum.shareit.item.model.item.Item;
import ru.practicum.shareit.item.model.item.ItemDto;
import ru.practicum.shareit.item.model.item.ItemMapper;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.model.UserDto;
import ru.practicum.shareit.user.service.UserServiceImpl;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.hasProperty;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@Transactional
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class ItemServiceImplTest {
    private final EntityManager em;
    private final ItemServiceImpl itemService;
    private final UserServiceImpl userService;
    private final BookingServiceImpl bookingService;

    private final UserDto firstUserDto = makeUserDto("user", "user@user.com");
    private final UserDto secondUserDto = makeUserDto("user2", "user2@user.com");
    private final ItemDto firstItemDto = makeItemDto("Дрель", "Простая дрель", true);
    private final ItemDto secondItemDto = makeItemDto("Отвёртка", "Простая отвертка", true);

    private final BookingDto bookingDto = makeBookingDto(
            LocalDateTime.now().minusMonths(1),
            LocalDateTime.now().minusMonths(1).plusDays(1),
            1L,
            new BookingDto.Item(1L, "Дрель"),
            new BookingDto.User(2L),
            Status.WAITING);

    private final CommentDto commentDto = makeCommentDto("Всё круто, 5 баллов");

    @BeforeEach
    void addData() {
        userService.addUser(firstUserDto);
    }


    @Test
    void addUser() {
        itemService.addItem(1L, firstItemDto);

        TypedQuery<Item> query = em.createQuery("Select i from Item i where i.id = :id", Item.class);
        Item targetItem = query
                .setParameter("id", 1L)
                .getSingleResult();

        assertThat(targetItem.getId(), notNullValue());
        assertThat(targetItem.getName(), equalTo(firstItemDto.getName()));
    }

    @Test
    void testGetItems() {
        List<ItemDto> itemDtoList = List.of(firstItemDto, secondItemDto);

        for (ItemDto itemDto : itemDtoList) {
            Item entity = ItemMapper.mapToItem(itemDto, 1L);
            em.persist(entity);
        }
        em.flush();

        Collection<ItemDto> targetItems = itemService.getItems(1L, 0, 10);

        assertThat(targetItems, hasSize(itemDtoList.size()));
        for (ItemDto itemDto : itemDtoList) {
            assertThat(targetItems, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("name", equalTo(itemDto.getName())))));
        }
    }

    @Test
    void testGetItemByItemIdAndUserId() {
        Item item = ItemMapper.mapToItem(firstItemDto, 1L);
        em.persist(item);
        em.flush();
        ItemDto targetItem = itemService.getItemByItemIdAndUserId(1L, 1L);

        assertThat(targetItem.getId(), notNullValue());
        assertThat(targetItem.getName(), equalTo(firstItemDto.getName()));
    }

    @Test
    void testUpdateItem() {
        Item item = ItemMapper.mapToItem(firstItemDto, 1L);
        em.persist(item);
        em.flush();
        ItemDto targetItem = itemService.updateItem(1L, 1L, secondItemDto);

        assertThat(targetItem.getId(), notNullValue());
        assertThat(targetItem.getName(), equalTo(secondItemDto.getName()));
    }

    @Test
    void testDeleteItem() {
        Item item = ItemMapper.mapToItem(firstItemDto, 1L);
        em.persist(item);
        em.flush();

        Collection<ItemDto> itemDtos = itemService.getItems(1L, 0, 10);
        assertThat(itemDtos, hasSize(1));

        itemService.deleteItem(1L);
        itemDtos = itemService.getItems(1L, 0, 10);
        assertThat(itemDtos, empty());
    }

    @Test
    void testSearchItems() {
        List<ItemDto> itemDtoList = List.of(firstItemDto, secondItemDto);

        for (ItemDto itemDto : itemDtoList) {
            Item entity = ItemMapper.mapToItem(itemDto, 1L);
            em.persist(entity);
        }
        em.flush();

        Collection<ItemDto> itemDtos = itemService.searchItems("Дрель", 0, 10);
        assertThat(itemDtos, hasSize(1));

        itemDtos = itemService.searchItems("Простая", 0, 10);
        assertThat(itemDtos, hasSize(2));
    }

    @Test
    void testAddComment() {
        Item item = ItemMapper.mapToItem(firstItemDto, 1L);
        em.persist(item);
        em.flush();

        userService.addUser(secondUserDto);
        bookingService.addBooking(2L, bookingDto);
        bookingService.setApprove(1L, 1L, true);

        itemService.addComment(2L, 1L, commentDto);

        ItemDto targetItem = itemService.getItemByItemIdAndUserId(1L, 1L);

        assertThat(targetItem.getComments().get(0).getId(), notNullValue());
        assertThat(targetItem.getComments().get(0).getText(), equalTo(commentDto.getText()));
        assertThat(targetItem.getComments().get(0).getAuthorName(), equalTo(commentDto.getAuthorName()));
        assertThat(targetItem.getComments().get(0).getCreated(), equalTo(commentDto.getCreated()));
    }

    private ItemDto makeItemDto(String name, String description, Boolean available) {
        ItemDto dto = new ItemDto();
        dto.setName(name);
        dto.setDescription(description);
        dto.setAvailable(available);
        return dto;
    }

    private UserDto makeUserDto(String name, String email) {
        UserDto dto = new UserDto();
        dto.setName(name);
        dto.setEmail(email);
        return dto;
    }

    private BookingDto makeBookingDto(
            LocalDateTime start, LocalDateTime end, Long itemId,
            BookingDto.Item item, BookingDto.User user, Status status)
    {
        BookingDto dto = new BookingDto();
        dto.setStart(start);
        dto.setEnd(end);
        dto.setItemId(itemId);
        dto.setItem(item);
        dto.setBooker(user);
        dto.setStatus(status);
        return dto;
    }

    private CommentDto makeCommentDto(String text) {
        CommentDto dto = new CommentDto();
        dto.setText(text);
        return dto;
    }
}
