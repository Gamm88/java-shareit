package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.model.*;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.service.BookingServiceImpl;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private BookingServiceImpl bookingService;

    private static final String USER_REQUEST_HEADER = "X-Sharer-User-id";

    private BookingDto bookingDto = new BookingDto(
            1L,
            LocalDateTime.of(2022, 12, 11, 00, 00),
            LocalDateTime.of(2022, 12, 15, 00, 00),
            1L,
            new BookingDto.Item(1L, "Дрель"),
            new BookingDto.User(2L),
            Status.APPROVED);

    @Test
    void addNewBooking() throws Exception {
        when(bookingService.addBooking(anyLong(), any(BookingDto.class)))
                .thenReturn(bookingDto);

        mockMvc.perform(post("/bookings")
                        .header(USER_REQUEST_HEADER, 1L)
                        .content(mapper.writeValueAsString(bookingDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect(jsonPath("$.itemId", is(1L), Long.class))
                .andExpect(jsonPath("$.item.id", is(1L), Long.class))
                .andExpect(jsonPath("$.item.name", is("Дрель")))
                .andExpect(jsonPath("$.booker.id", is(2L), Long.class))
                .andExpect(jsonPath("$.status", is("APPROVED")));

        verify(bookingService, times(1)).addBooking(anyLong(), any(BookingDto.class));
    }
}

    /*


@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private BookingServiceImpl bookingService;

    private static final String USER_REQUEST_HEADER = "X-Sharer-User-id";

    private BookingDto bookingDto = new BookingDto(
            1L,
            LocalDateTime.of(2022, 11, 11, 00, 00),
            LocalDateTime.of(2022, 11, 15, 00, 00),
            1L,
            new BookingDto.Item(1L, "Дрель"),
            new BookingDto.User(2L),
            Status.APPROVED);

    /*
    private BookingDto bookingDto = new BookingDto(
            1L,
            LocalDateTime.of(2022, 11, 11, 00, 00),
            LocalDateTime.of(2022, 11, 15, 00, 00),
            1L,
            new BookingDto.Item(1L, "Дрель"),
            new BookingDto.User(2L),
            Status.WAITING);

    private final CommentDto commentDto = new CommentDto(
            1L, "Дрель грязная!", "Василий", LocalDateTime.of(2022, 12, 12, 15, 00));



    @Test
    void addNewBooking() throws Exception {
        when(bookingService.addBooking(anyLong(), any(BookingDto.class)))
                .thenReturn(bookingDto);

        mockMvc.perform(post("/bookings")
                        .header(USER_REQUEST_HEADER, 1L)
                        .content(mapper.writeValueAsString(bookingDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect(jsonPath("$.itemId", is(1L), Long.class))
                .andExpect(jsonPath("$.item.id", is(1L), Long.class))
                .andExpect(jsonPath("$.item.name", is("testName")))
                .andExpect(jsonPath("$.booker.id", is(1L), Long.class))
                .andExpect(jsonPath("$.status", is("APPROVED")));

        verify(bookingService, times(1)).addBooking(anyLong(), any(BookingDto.class));
    }

    /*
    @Test
    void testGetBookings() throws Exception {
        BookingDto newBookingDto = new BookingDto(
                1L, "Отвертка", "Аккумуляторная отвертка", true, null, null, null,
                new ArrayList<>());

        when(bookingService.getBookings(1L, 0, 10))
                .thenReturn(List.of(bookingDto, newBookingDto));

        mockMvc.perform(get("/bookings/")
                        .content(mapper.writeValueAsString(bookingDto))
                        .header(USER_REQUEST_HEADER, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(bookingDto.getName())))
                .andExpect(jsonPath("$[0].description", is(bookingDto.getDescription())))
                .andExpect(jsonPath("$[0].available", is(bookingDto.getAvailable())))
                .andExpect(jsonPath("$[1].id", is(newBookingDto.getId()), Long.class))
                .andExpect(jsonPath("$[1].name", is(newBookingDto.getName())))
                .andExpect(jsonPath("$[1].description", is(newBookingDto.getDescription())))
                .andExpect(jsonPath("$[1].available", is(newBookingDto.getAvailable())));

        verify(bookingService, Mockito.times(1)).getBookings(1L, 0, 10);
    }

    @Test
    void testGetBookingByBookingIdAndUserId() throws Exception {
        when(bookingService.getBookingByBookingIdAndUserId(anyLong(), anyLong()))
                .thenReturn(bookingDto);

        mockMvc.perform(get("/bookings/1")
                        .content(mapper.writeValueAsString(bookingDto))
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

        Mockito.verify(bookingService, Mockito.times(1)).getBookingByBookingIdAndUserId(anyLong(), anyLong());
    }

    @Test
    void testUpdateBooking() throws Exception {
        bookingDto.setName("Новая отвертка");
        bookingDto.setDescription("Новая простая дрель");
        when(bookingService.updateBooking(1L, 1L, bookingDto))
                .thenReturn(bookingDto);

        mockMvc.perform(patch("/bookings/{bookingId}", 1)
                        .content(mapper.writeValueAsString(bookingDto))
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

        Mockito.verify(bookingService, Mockito.times(1)).updateBooking(1L, 1L, bookingDto);
    }

    @Test
    void testDeleteBooking() throws Exception {
        mockMvc.perform(delete("/bookings/{bookingId}", 1)
                        .content(mapper.writeValueAsString(bookingDto))
                        .header(USER_REQUEST_HEADER, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk());

        Mockito.verify(bookingService, Mockito.times(1)).deleteBooking(anyLong());
    }

    @Test
    void getBookingsBySearch() throws Exception {
        when(bookingService.searchBookings("Дрель", 0, 10))
                .thenReturn(List.of(bookingDto));

        mockMvc.perform(get("/bookings/search?text=Дрель")
                        .content(mapper.writeValueAsString(bookingDto))
                        .header(USER_REQUEST_HEADER, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(bookingDto.getName())))
                .andExpect(jsonPath("$[0].description", is(bookingDto.getDescription())))
                .andExpect(jsonPath("$[0].available", is(bookingDto.getAvailable())));

        verify(bookingService, Mockito.times(1)).searchBookings("Дрель", 0, 10);
    }

    @Test
    void testAddComment() throws Exception {
        when(bookingService.addComment(1L, 1L, commentDto))
                .thenReturn(commentDto);

        mockMvc.perform(post("/bookings/1/comment")
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
    void testGetBookingWithNotFoundException() throws Exception {
        when(bookingService.getBookingByBookingIdAndUserId(anyLong(), anyLong()))
                .thenThrow(new NotFoundException("Такого вещи не существует"));

        mockMvc.perform(get("/Bookings/99")
                        .content(mapper.writeValueAsString(bookingDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(404));
    }
}
     */