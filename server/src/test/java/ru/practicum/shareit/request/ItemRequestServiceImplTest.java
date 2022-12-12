package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.item.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.ItemRequestDto;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestServiceImplTest {
    @Autowired
    private final ItemRequestServiceImpl itemRequestService;

    @MockBean
    private final ItemRequestRepository itemRequestRepository;

    @MockBean
    private final UserRepository userRepository;

    @MockBean
    private final ItemRepository itemRepository;

    @Test
    void testAddItemRequest() {
        LocalDateTime now = LocalDateTime.now();

        User user = User.builder()
                .id(1L)
                .name("name")
                .email("user@mail.com")
                .build();
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        ItemRequestDto requestDto = ItemRequestDto.builder()
                .description("description")
                .build();

        ItemRequest request = ItemRequest.builder()
                .id(1L)
                .description("description")
                .requestor(user)
                .created(now)
                .build();
        when(itemRequestRepository.save(any()))
                .thenReturn(request);

        ItemRequestDto itemRequestDtoCreated = itemRequestService.addItemRequest(1L, requestDto);
        assertThat(itemRequestDtoCreated, is(notNullValue()));
    }


    @Test
    void throwUserNotFoundException() {
        when(userRepository.findById(1L))
                .thenReturn(Optional.empty());

        ItemRequestDto requestDto = ItemRequestDto.builder()
                .description("description")
                .build();

        NotFoundException userNotFoundException;

        userNotFoundException = Assertions.assertThrows(NotFoundException.class,
                () -> itemRequestService.addItemRequest(2L, requestDto));
        assertThat(userNotFoundException.getMessage(), is("Пользователь с ИД 2 не найден."));

        userNotFoundException = Assertions.assertThrows(NotFoundException.class,
                () -> itemRequestService.getItemRequestsByUser(2L));
        assertThat(userNotFoundException.getMessage(), is("Пользователь с ИД 2 не найден."));

        userNotFoundException = Assertions.assertThrows(NotFoundException.class,
                () -> itemRequestService.getRequestsOtherUsers(2L, 0, 10));
        assertThat(userNotFoundException.getMessage(), is("Пользователь с ИД 2 не найден."));

        userNotFoundException = Assertions.assertThrows(NotFoundException.class,
                () -> itemRequestService.getItemRequestByUser(2L, 1L));
        assertThat(userNotFoundException.getMessage(), is("Пользователь с ИД 2 не найден."));
    }


    @Test
    void testGetItemRequestsByUser() {
        User owner = User.builder()
                .id(1L)
                .name("name")
                .email("user@mail.com")
                .build();
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(owner));

        when(itemRequestRepository.findAllByRequestor_IdOrderByCreatedDesc(1L))
                .thenReturn(new ArrayList<>());

        List<ItemRequestDto> itemRequestDtos = itemRequestService.getItemRequestsByUser(1L);
        assertTrue(itemRequestDtos.isEmpty());

        LocalDateTime requestCreationDate = LocalDateTime.now();

        ItemRequest request = ItemRequest.builder()
                .id(1L)
                .description("description")
                .requestor(owner)
                .created(requestCreationDate)
                .build();

        List<ItemRequest> itemRequests = List.of(request);
        when(itemRequestRepository.findAllByRequestor_IdOrderByCreatedDesc(1L))
                .thenReturn(itemRequests);
    }


    @Test
    void testGetRequestsOtherUsers() {
        User owner = User.builder()
                .id(1L)
                .name("name1")
                .email("user1@email.com")
                .build();
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(owner));

        LocalDateTime requestCreationDate = LocalDateTime.now();

        User requestor = User.builder()
                .id(2L)
                .name("name2")
                .email("user2@email.com")
                .build();

        ItemRequest request = ItemRequest.builder()
                .id(1L)
                .description("description")
                .requestor(requestor)
                .created(requestCreationDate)
                .build();

        List<ItemRequest> itemRequests = new ArrayList<>();

        when(itemRequestRepository.findAllByRequestor_IdIsNotOrderByCreatedDesc(any(), any()))
                .thenReturn(itemRequests);

        List<ItemRequestDto> itemRequestDtos = itemRequestService.getRequestsOtherUsers(1L, 0, 10);
        assertTrue(itemRequestDtos.isEmpty());

        itemRequests = List.of(request);
        when(itemRequestRepository.findAllByRequestor_IdIsNotOrderByCreatedDesc(any(), any()))
                .thenReturn(itemRequests);

        itemRequestDtos = itemRequestService.getRequestsOtherUsers(1L, 0, 10);
        assertTrue(itemRequestDtos.get(0).getItems().isEmpty());
    }

    @Test
    void testGetItemRequestByUser() {
        User owner = User.builder()
                .id(1L)
                .name("name1")
                .email("user1@email.com")
                .build();

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(owner));

        LocalDateTime requestCreationDate = LocalDateTime.now();

        ItemRequest request = ItemRequest.builder()
                .id(1L)
                .description("description")
                .requestor(owner)
                .created(requestCreationDate)
                .build();

        when(itemRequestRepository.findById(request.getId()))
                .thenReturn(Optional.of(request));

        List<Item> items = Collections.emptyList();
        when(itemRepository.findAllByRequest_Id(1L))
                .thenReturn(items);

        ItemRequestDto itemRequestDto = itemRequestService.getItemRequestByUser(1L, 1L);
        assertTrue(itemRequestDto.getItems().isEmpty());

        Item item = Item.builder()
                .id(1L)
                .name("name")
                .description("description")
                .available(true)
                .request(request)
                .build();
        items = List.of(item);

        when(itemRepository.findAllByRequest_Id(1L))
                .thenReturn(items);

        itemRequestDto = itemRequestService.getItemRequestByUser(1L, 1L);

        assertThat(itemRequestDto, is(notNullValue()));
    }

    @Test
    void throwItemRequestNotFoundException() {
        User owner = User.builder()
                .id(1L)
                .name("name1")
                .email("user1@email.com")
                .build();

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(owner));

        when(itemRequestRepository.findById(any()))
                .thenReturn(Optional.empty());

        NotFoundException invalidItemRequestIdException = Assertions.assertThrows(NotFoundException.class,
                () -> itemRequestService.getItemRequestOrNotFound(1L));
        assertThat(invalidItemRequestIdException.getMessage(), is("Запрос вещи с ИД 1 не найден."));
    }
}