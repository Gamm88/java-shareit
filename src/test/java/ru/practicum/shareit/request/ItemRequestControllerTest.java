package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import ru.practicum.shareit.user.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.model.item.ItemDto;
import ru.practicum.shareit.request.model.ItemRequestDto;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.shareit.request.service.ItemRequestService;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import java.util.List;
import java.util.ArrayList;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.mockito.Mockito.*;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
public class ItemRequestControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemRequestService itemRequestService;

    private static final String USER_REQUEST_HEADER = "X-Sharer-User-id";

    private final User user = new User(1L, "user", "user@user.com");

    private final ItemDto itemDto = new ItemDto(
            1L, "Дрель", "Простая дрель", true, null,
            new ItemDto.ItemBooking(1L, 1L),
            new ItemDto.ItemBooking(2L, 1L),
            new ArrayList<>());

    private final ItemRequestDto itemRequestDto = new ItemRequestDto(
            1L,
            "description",
            user,
            LocalDateTime.of(2022, 11, 11, 0, 0),
            null);

    @Test
    void testAddItemRequest() throws Exception {
        when(itemRequestService.addItemRequest(1L, itemRequestDto))
                .thenReturn(itemRequestDto);

        mockMvc.perform(post("/requests")
                        .header(USER_REQUEST_HEADER, 1L)
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$.requestor.id", is(itemRequestDto.getRequestor().getId()), Long.class))
                .andExpect(jsonPath("$.created",
                        is(itemRequestDto.getCreated().format(DateTimeFormatter.ISO_DATE_TIME))));

        verify(itemRequestService, times(1)).addItemRequest(anyLong(), any(ItemRequestDto.class));
    }


    @Test
    void testGetItemRequestsByUser() throws Exception {
        itemRequestDto.setItems(List.of(itemDto));

        when(itemRequestService.getItemRequestsByUser(1L))
                .thenReturn(List.of(itemRequestDto));

        mockMvc.perform(get("/requests")
                        .header(USER_REQUEST_HEADER, 1L)
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$[0].requestor.id", is(itemRequestDto.getRequestor().getId()), Long.class))
                .andExpect(jsonPath("$[0].created",
                        is(itemRequestDto.getCreated().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$[0].items[0].id",
                        is(itemRequestDto.getItems().get(0).getId()), Long.class))
                .andExpect(jsonPath("$[0].items[0].name",
                        is(itemRequestDto.getItems().get(0).getName())))
                .andExpect(jsonPath("$[0].items[0].description",
                        is(itemRequestDto.getItems().get(0).getDescription())))
                .andExpect(jsonPath("$[0].items[0].available",
                        is(itemRequestDto.getItems().get(0).getAvailable())))
                .andExpect(jsonPath("$[0].items[0].requestId",
                        is(itemRequestDto.getItems().get(0).getRequestId()), Long.class));

        verify(itemRequestService, times(1)).getItemRequestsByUser(anyLong());
    }


    @Test
    void testGetRequestsOtherUsers() throws Exception {
        itemRequestDto.setItems(List.of(itemDto));

        when(itemRequestService.getRequestsOtherUsers(2L, 0, 10))
                .thenReturn(List.of(itemRequestDto));

        mockMvc.perform(get("/requests/all")
                        .header(USER_REQUEST_HEADER, 2L)
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$[0].requestor.id", is(itemRequestDto.getRequestor().getId()), Long.class))
                .andExpect(jsonPath("$[0].created",
                        is(itemRequestDto.getCreated().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$[0].items[0].id",
                        is(itemRequestDto.getItems().get(0).getId()), Long.class))
                .andExpect(jsonPath("$[0].items[0].name",
                        is(itemRequestDto.getItems().get(0).getName())))
                .andExpect(jsonPath("$[0].items[0].description",
                        is(itemRequestDto.getItems().get(0).getDescription())))
                .andExpect(jsonPath("$[0].items[0].available",
                        is(itemRequestDto.getItems().get(0).getAvailable())))
                .andExpect(jsonPath("$[0].items[0].requestId",
                        is(itemRequestDto.getItems().get(0).getRequestId()), Long.class));
    }


    @Test
    void testGetRequest() throws Exception {
        itemRequestDto.setItems(List.of(itemDto));

        when(itemRequestService.getItemRequestByUser(1L, 1L))
                .thenReturn(itemRequestDto);

        mockMvc.perform(get("/requests/1")
                        .header(USER_REQUEST_HEADER, 1L)
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$.requestor.id", is(itemRequestDto.getRequestor().getId()), Long.class))
                .andExpect(jsonPath("$.created",
                        is(itemRequestDto.getCreated().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$.items[0].id",
                        is(itemRequestDto.getItems().get(0).getId()), Long.class))
                .andExpect(jsonPath("$.items[0].name",
                        is(itemRequestDto.getItems().get(0).getName())))
                .andExpect(jsonPath("$.items[0].description",
                        is(itemRequestDto.getItems().get(0).getDescription())))
                .andExpect(jsonPath("$.items[0].available",
                        is(itemRequestDto.getItems().get(0).getAvailable())));
    }
}