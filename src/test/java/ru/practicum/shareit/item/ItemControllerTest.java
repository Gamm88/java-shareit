package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.comment.CommentDto;
import ru.practicum.shareit.item.model.item.*;
import ru.practicum.shareit.item.service.ItemServiceImpl;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ItemServiceImpl itemService;

    private static final String USER_REQUEST_HEADER = "X-Sharer-User-id";

    private final CommentDto commentDto = new CommentDto(
            1L, "Дрель грязная!", "Василий", LocalDateTime.of(2022, 12, 12, 15, 00));

    private final ItemDto itemDto = new ItemDto(
            1L, "Дрель", "Простая дрель", true, null,
            new ItemDto.ItemBooking(1L, 1L),
            new ItemDto.ItemBooking(2L, 1L),
            new ArrayList<>());

    @Test
    void testAddItem() throws Exception {
        when(itemService.addItem(anyLong(), any()))
                .thenReturn(itemDto);

        mockMvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .header(USER_REQUEST_HEADER, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect(jsonPath("$.name", is("Дрель")))
                .andExpect(jsonPath("$.description", is("Простая дрель")))
                .andExpect(jsonPath("$.available", is(true)));

        verify(itemService, Mockito.times(1)).addItem(anyLong(), any());
    }

    @Test
    void testGetItems() throws Exception {
        ItemDto newItemDto = new ItemDto(
                1L, "Отвертка", "Аккумуляторная отвертка", true, null, null, null,
                new ArrayList<>());

        when(itemService.getItems(1L, 0, 10))
                .thenReturn(List.of(itemDto, newItemDto));

        mockMvc.perform(get("/items/")
                        .content(mapper.writeValueAsString(itemDto))
                        .header(USER_REQUEST_HEADER, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemDto.getName())))
                .andExpect(jsonPath("$[0].description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$[1].id", is(newItemDto.getId()), Long.class))
                .andExpect(jsonPath("$[1].name", is(newItemDto.getName())))
                .andExpect(jsonPath("$[1].description", is(newItemDto.getDescription())))
                .andExpect(jsonPath("$[1].available", is(newItemDto.getAvailable())));

        verify(itemService, Mockito.times(1)).getItems(1L, 0, 10);
    }

    @Test
    void testGetItemByItemIdAndUserId() throws Exception {
        when(itemService.getItemByItemIdAndUserId(anyLong(), anyLong()))
                .thenReturn(itemDto);

        mockMvc.perform(get("/items/1")
                        .content(mapper.writeValueAsString(itemDto))
                        .header(USER_REQUEST_HEADER, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect(jsonPath("$.name", is("Дрель")))
                .andExpect(jsonPath("$.description", is("Простая дрель")))
                .andExpect(jsonPath("$.available", is(true)));

        Mockito.verify(itemService, Mockito.times(1)).getItemByItemIdAndUserId(anyLong(), anyLong());
    }

    @Test
    void testUpdateItem() throws Exception {
        itemDto.setName("Новая отвертка");
        itemDto.setDescription("Новая простая дрель");
        when(itemService.updateItem(1L, 1L, itemDto))
                .thenReturn(itemDto);

        mockMvc.perform(patch("/items/{itemId}", 1)
                        .content(mapper.writeValueAsString(itemDto))
                        .header(USER_REQUEST_HEADER, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect(jsonPath("$.name", is("Новая отвертка")))
                .andExpect(jsonPath("$.description", is("Новая простая дрель")))
                .andExpect(jsonPath("$.available", is(true)));

        Mockito.verify(itemService, Mockito.times(1)).updateItem(1L, 1L, itemDto);
    }

    @Test
    void testDeleteItem() throws Exception {
        mockMvc.perform(delete("/items/{itemId}", 1)
                        .content(mapper.writeValueAsString(itemDto))
                        .header(USER_REQUEST_HEADER, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk());

        Mockito.verify(itemService, Mockito.times(1)).deleteItem(anyLong());
    }

    @Test
    void getItemsBySearch() throws Exception {
        when(itemService.searchItems("Дрель", 0, 10))
                .thenReturn(List.of(itemDto));

        mockMvc.perform(get("/items/search?text=Дрель")
                        .content(mapper.writeValueAsString(itemDto))
                        .header(USER_REQUEST_HEADER, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemDto.getName())))
                .andExpect(jsonPath("$[0].description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemDto.getAvailable())));

        verify(itemService, Mockito.times(1)).searchItems("Дрель", 0, 10);
    }

    @Test
    void testAddComment() throws Exception {
        when(itemService.addComment(1L, 1L, commentDto))
                .thenReturn(commentDto);

        mockMvc.perform(post("/items/1/comment")
                        .content(mapper.writeValueAsString(commentDto))
                        .header(USER_REQUEST_HEADER, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(commentDto.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentDto.getText())))
                .andExpect(jsonPath("$.authorName", is(commentDto.getAuthorName())))
                .andExpect(jsonPath("$.created", is("2022-12-12T15:00:00")));
    }

    @Test
    void testGetItemWithNotFoundException() throws Exception {
        when(itemService.getItemByItemIdAndUserId(anyLong(), anyLong()))
                .thenThrow(new NotFoundException("Такого вещи не существует"));

        mockMvc.perform(get("/Items/99")
                        .content(mapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(404));
    }
}