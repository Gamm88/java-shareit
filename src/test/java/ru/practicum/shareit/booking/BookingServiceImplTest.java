package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingDto;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidatorExceptions;
import ru.practicum.shareit.item.model.item.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;
import java.util.Collections;
import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceImplTest {
    @Autowired
    private final BookingServiceImpl bookingService;

    @MockBean
    private final BookingRepository bookingRepository;

    @MockBean
    private final UserRepository userRepository;

    @MockBean
    private final ItemRepository itemRepository;

    @Test
    void testAddNewBooking() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        BookingDto bookingDto = BookingDto.builder()
                .start(start)
                .end(end)
                .itemId(1L)
                .build();

        User owner = User.builder()
                .id(1L)
                .name("user1")
                .email("user1@mail.com")
                .build();

        Item item = Item.builder()
                .id(1L)
                .owner(owner.getId())
                .name("name")
                .description("description")
                .available(true)
                .build();
        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item));

        User booker = User.builder()
                .id(3L)
                .name("user3")
                .email("user3@mail.com")
                .build();
        when(userRepository.findById(3L))
                .thenReturn(Optional.of(booker));

        Booking booking = Booking.builder()
                .id(1L)
                .start(start)
                .end(end)
                .item(item)
                .booker(booker)
                .status(Status.WAITING)
                .build();
        when(bookingRepository.save(any()))
                .thenReturn(booking);

        BookingDto createdBookingDto = bookingService.addBooking(3L, bookingDto);
        assertThat(createdBookingDto, is(notNullValue()));
    }

    @Test
    void testThrowItemNotFoundException() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        BookingDto bookingDto = BookingDto.builder()
                .start(start)
                .end(end)
                .itemId(1L)
                .build();

        NotFoundException itemNotFoundException;

        when(itemRepository.findById(1L)).thenReturn(Optional.empty());
        itemNotFoundException = Assertions.assertThrows(NotFoundException.class,
                () -> bookingService.addBooking(1L, bookingDto));
        assertThat(itemNotFoundException.getMessage(), is("Вещь с ИД 1 не найден."));
    }

    @Test
    void testThrowUserOrNotFoundException() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        BookingDto bookingDto = BookingDto.builder()
                .start(start)
                .end(end)
                .itemId(1L)
                .build();

        User owner = User.builder()
                .id(1L)
                .name("user1")
                .email("user1@email.com")
                .build();

        Item item = Item.builder()
                .id(1L)
                .owner(owner.getId())
                .name("name")
                .description("description")
                .available(false)
                .build();
        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item));

        NotFoundException userNotFoundException;

        userNotFoundException = Assertions.assertThrows(NotFoundException.class,
                () -> bookingService.addBooking(3L, bookingDto));
        assertThat(userNotFoundException.getMessage(), is("Пользователь с ИД 3 не найден."));
    }

    @Test
    void testThrowValidatorExceptions() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        BookingDto bookingDto = BookingDto.builder()
                .start(start)
                .end(end)
                .itemId(1L)
                .build();

        User owner = User.builder()
                .id(1L)
                .name("user1")
                .email("user1@email.com")
                .build();

        Item item = Item.builder()
                .id(1L)
                .owner(owner.getId())
                .name("name")
                .description("description")
                .available(false)
                .build();
        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item));

        User booker = User.builder()
                .id(3L)
                .name("user3")
                .email("user3@email.com")
                .build();
        when(userRepository.findById(3L))
                .thenReturn(Optional.of(booker));

        ValidatorExceptions validatorExceptions;

        validatorExceptions = Assertions.assertThrows(ValidatorExceptions.class,
                () -> bookingService.addBooking(3L, bookingDto));
        assertThat(validatorExceptions.getMessage(), is("Вещь недоступна для аренды"));
    }

    @Test
    void testExceptionOwnerItemCannotBookHisItem() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        BookingDto bookingDto = BookingDto.builder()
                .start(start)
                .end(end)
                .itemId(1L)
                .build();

        User owner = User.builder()
                .id(1L)
                .name("user1")
                .email("user1@email.com")
                .build();
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(owner));

        Item item = Item.builder()
                .id(1L)
                .owner(owner.getId())
                .name("name")
                .description("description")
                .available(true)
                .build();
        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item));

        NotFoundException userNotFoundException;

        userNotFoundException = Assertions.assertThrows(NotFoundException.class,
                () -> bookingService.addBooking(1L, bookingDto));
        assertThat(userNotFoundException.getMessage(), is("Владелец вещи не может бронировать свою вещь"));
    }

    @Test
    void testExceptionStartBookingEarlierThanEndBooking() {
        LocalDateTime start = LocalDateTime.now().plusDays(2);
        LocalDateTime end = LocalDateTime.now().plusDays(1);

        BookingDto bookingDto = BookingDto.builder()
                .start(start)
                .end(end)
                .itemId(1L)
                .build();

        User owner = User.builder()
                .id(1L)
                .name("user1")
                .email("user1@email.com")
                .build();

        Item item = Item.builder()
                .id(1L)
                .owner(owner.getId())
                .name("name")
                .description("description")
                .available(true)
                .build();
        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item));

        User booker = User.builder()
                .id(3L)
                .name("user3")
                .email("user3@email.com")
                .build();
        when(userRepository.findById(3L))
                .thenReturn(Optional.of(booker));

        ValidatorExceptions validatorExceptions;

        validatorExceptions = Assertions.assertThrows(ValidatorExceptions.class,
                () -> bookingService.addBooking(3L, bookingDto));
        assertThat(validatorExceptions.getMessage(), is("Начало бронирования раньше, времени окончания бронирования"));
    }

    @Test
    void testSetApprove() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        User owner = User.builder()
                .id(1L)
                .name("user1")
                .email("user1@email.com")
                .build();
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(owner));

        Item item = Item.builder()
                .id(1L)
                .owner(owner.getId())
                .name("name")
                .description("description")
                .available(true)
                .build();
        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item));

        User booker = User.builder()
                .id(3L)
                .name("user3")
                .email("user3@email.com")
                .build();
        when(userRepository.findById(3L))
                .thenReturn(Optional.of(booker));

        Booking booking = Booking.builder()
                .id(1L)
                .start(start)
                .end(end)
                .item(item)
                .booker(booker)
                .status(Status.WAITING)
                .build();
        when(bookingRepository.findById(1L))
                .thenReturn(Optional.of(booking));

        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking);

        BookingDto approveBookingDto = bookingService.setApprove(1L, 1L, true);
        assertThat(approveBookingDto, is(notNullValue()));

        booking.setStatus(Status.WAITING);
        approveBookingDto = bookingService.setApprove(1L, 1L, false);
        assertThat(approveBookingDto, is(notNullValue()));

        booking.setStatus(Status.WAITING);
        when(bookingRepository.findById(1L))
                .thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class)))
                .thenAnswer(
                        invocation -> {
                            Booking invoc = invocation.getArgument(0, Booking.class);
                            invoc.setStatus(Status.APPROVED);
                            return invoc;
                        }
                );

        approveBookingDto = bookingService.setApprove(1L, 1L, true);
        assertThat(approveBookingDto, is(notNullValue()));

        booking.setStatus(Status.WAITING);
        when(bookingRepository.findById(1L))
                .thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class)))
                .thenAnswer(
                        invocation -> {
                            Booking invoc = invocation.getArgument(0, Booking.class);
                            invoc.setStatus(Status.REJECTED);
                            return invoc;
                        }
                );
        approveBookingDto = bookingService.setApprove(1L, 1L, false);
        assertThat(approveBookingDto, is(notNullValue()));
        Assertions.assertEquals(approveBookingDto.getStatus(), Status.REJECTED);
    }

    @Test
    void testThrowBookingNotFoundException() {
        when(bookingRepository.findById(1L))
                .thenReturn(Optional.empty());

        NotFoundException notFoundException;

        notFoundException = Assertions.assertThrows(NotFoundException.class,
                () -> bookingService.setApprove(1L, 1L, true));
        assertThat(notFoundException.getMessage(), is("Аренда с ИД 1 не найден."));
    }

    @Test
    void testFindByBookingIdAndUserId() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        User owner = User.builder()
                .id(1L)
                .name("user1")
                .email("user1@email.com")
                .build();
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(owner));

        Item item = Item.builder()
                .id(1L)
                .owner(owner.getId())
                .name("name")
                .description("description")
                .available(true)
                .build();
        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item));

        User booker = User.builder()
                .id(3L)
                .name("user3")
                .email("user3@email.com")
                .build();
        when(userRepository.findById(3L))
                .thenReturn(Optional.of(booker));

        Booking booking = Booking.builder()
                .id(1L)
                .start(start)
                .end(end)
                .item(item)
                .booker(booker)
                .status(Status.WAITING)
                .build();
        when(bookingRepository.findById(1L))
                .thenReturn(Optional.of(booking));

        BookingDto getBookingDto = bookingService.findByBookingIdAndUserId(1L, 1L);
        assertThat(getBookingDto, is(notNullValue()));

        getBookingDto = bookingService.findByBookingIdAndUserId(1L, 3L);
        assertThat(getBookingDto, is(notNullValue()));
    }

    @Test
    void testFindBookingsByUserIdAndState() {
        LocalDateTime start = LocalDateTime.now().plusDays(1L);
        LocalDateTime end = LocalDateTime.now().plusDays(2L);

        User owner = User.builder()
                .id(1L)
                .name("user1")
                .email("user1@email.com")
                .build();
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(owner));

        Item item = Item.builder()
                .id(1L)
                .owner(owner.getId())
                .name("name")
                .description("description")
                .available(true)
                .build();
        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item));

        User booker = User.builder()
                .id(3L)
                .name("user3")
                .email("user3@email.com")
                .build();
        when(userRepository.findById(3L))
                .thenReturn(Optional.of(booker));

        Booking booking = Booking.builder()
                .id(1L)
                .start(start)
                .end(end)
                .item(item)
                .booker(booker)
                .status(Status.WAITING)
                .build();
        when(bookingRepository.findById(1L))
                .thenReturn(Optional.of(booking));

        when(bookingRepository.findAllByBooker_IdOrderByStartDesc(any(), any()))
                .thenReturn(List.of(booking));
        List<BookingDto> BookingDtoList = (List<BookingDto>) bookingService
                .findBookingsByUserIdAndState(3L, "ALL", 0, 10);
        Assertions.assertFalse(BookingDtoList.isEmpty());

        start = LocalDateTime.now().minusDays(1L);
        end = LocalDateTime.now().plusDays(2L);
        Booking booking4 = Booking.builder()
                .id(4L)
                .start(start)
                .end(end)
                .item(item)
                .booker(booker)
                .status(Status.APPROVED)
                .build();
        when(bookingRepository.findAllByBooker_IdAndStatusCurrentOrderByStartDesc(any(), any(), any()))
                .thenReturn(List.of(booking4));
        BookingDtoList = (List<BookingDto>) bookingService
                .findBookingsByUserIdAndState(3L, "CURRENT", 0, 10);
        Assertions.assertFalse(BookingDtoList.isEmpty());

        start = LocalDateTime.now().plusDays(1L);
        end = LocalDateTime.now().plusDays(2L);
        Booking booking3 = Booking.builder()
                .id(3L)
                .start(start)
                .end(end)
                .item(item)
                .booker(booker)
                .status(Status.APPROVED)
                .build();
        when(bookingRepository.findAllByBooker_IdAndStartIsAfterOrderByStartDesc(any(), any(), any()))
                .thenReturn(List.of(booking3));
        BookingDtoList = (List<BookingDto>) bookingService
                .findBookingsByUserIdAndState(3L, "FUTURE", 0, 10);
        Assertions.assertFalse(BookingDtoList.isEmpty());

        start = LocalDateTime.now().minusDays(2L);
        end = LocalDateTime.now().minusDays(1L);
        Booking booking2 = Booking.builder()
                .id(2L)
                .start(start)
                .end(end)
                .item(item)
                .booker(booker)
                .status(Status.APPROVED)
                .build();
        when(bookingRepository.findAllByBooker_IdAndEndIsBeforeOrderByStartDesc(any(), any(), any()))
                .thenReturn(List.of(booking2));
        BookingDtoList = (List<BookingDto>) bookingService
                .findBookingsByUserIdAndState(3L, "PAST", 0, 10);
        Assertions.assertFalse(BookingDtoList.isEmpty());

        start = LocalDateTime.now().plusDays(1L);
        end = LocalDateTime.now().plusDays(2L);
        Booking booking5 = Booking.builder()
                .id(5L)
                .start(start)
                .end(end)
                .item(item)
                .booker(booker)
                .status(Status.APPROVED)
                .build();

        when(bookingRepository
                .findAllByBooker_IdAndStatusOrderByStartDesc(any(Long.class), any(Status.class), any(PageRequest.class)))
                .thenAnswer(
                        invocation -> {
                            Long userId = invocation.getArgument(0, Long.class);
                            Status status = invocation.getArgument(1, Status.class);
                            if (status.equals(Status.WAITING) && userId.equals(3L)) {
                                booking5.setStatus(Status.WAITING);
                                return List.of(booking5);
                            }
                            if (status.equals(Status.REJECTED) && userId.equals(3L)) {
                                booking5.setStatus(Status.REJECTED);
                                return List.of(booking5);
                            }
                            return Collections.emptyList();
                        }

                );

        User booker6 = User.builder()
                .id(6L)
                .name("user6")
                .email("user6@email.com")
                .build();
        when(userRepository.findById(6L))
                .thenReturn(Optional.of(booker6));

        BookingDtoList = (List<BookingDto>) bookingService
                .findBookingsByUserIdAndState(3L, "WAITING", 0, 10);
        Assertions.assertFalse(BookingDtoList.isEmpty());
        assertThat(BookingDtoList.get(0).getStatus(), is(Status.WAITING));

        BookingDtoList = (List<BookingDto>) bookingService
                .findBookingsByUserIdAndState(3L, "REJECTED", 0, 10);
        Assertions.assertFalse(BookingDtoList.isEmpty());
        assertThat(BookingDtoList.get(0).getStatus(), is(Status.REJECTED));

        BookingDtoList = (List<BookingDto>) bookingService
                .findBookingsByUserIdAndState(6L, "WAITING", 0, 10);
        Assertions.assertTrue(BookingDtoList.isEmpty());
    }

    @Test
    void testFindBookingsByOwnerIdAndState() {
        LocalDateTime start = LocalDateTime.now().plusDays(1L);
        LocalDateTime end = LocalDateTime.now().plusDays(2L);

        User owner = User.builder()
                .id(1L)
                .name("user1")
                .email("user1@email.com")
                .build();
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(owner));

        Item item = Item.builder()
                .id(1L)
                .owner(owner.getId())
                .name("name")
                .description("description")
                .available(true)
                .build();
        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item));

        User booker = User.builder()
                .id(3L)
                .name("user3")
                .email("user3@email.com")
                .build();
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(booker));

        Booking booking = Booking.builder()
                .id(1L)
                .start(start)
                .end(end)
                .item(item)
                .booker(booker)
                .status(Status.WAITING)
                .build();
        when(bookingRepository.findById(1L))
                .thenReturn(Optional.of(booking));

        when(bookingRepository.findAllByItem_OwnerOrderByStartDesc(any(), any()))
                .thenReturn(List.of(booking));
        List<BookingDto> BookingDtoList = (List<BookingDto>) bookingService
                .findBookingsByOwnerIdAndState(1L, "ALL", 0, 10);
        Assertions.assertFalse(BookingDtoList.isEmpty());

        start = LocalDateTime.now().minusDays(1L);
        end = LocalDateTime.now().plusDays(2L);
        Booking booking4 = Booking.builder()
                .id(4L)
                .start(start)
                .end(end)
                .item(item)
                .booker(booker)
                .status(Status.APPROVED)
                .build();
        when(bookingRepository.findAllByItem_OwnerAndStatusCurrentOrderByStartDesc(any(), any(), any()))
                .thenReturn(List.of(booking4));
        BookingDtoList = (List<BookingDto>) bookingService
                .findBookingsByOwnerIdAndState(1L, "CURRENT", 0, 10);
        Assertions.assertFalse(BookingDtoList.isEmpty());

        start = LocalDateTime.now().plusDays(1L);
        end = LocalDateTime.now().plusDays(2L);
        Booking booking3 = Booking.builder()
                .id(3L)
                .start(start)
                .end(end)
                .item(item)
                .booker(booker)
                .status(Status.APPROVED)
                .build();
        when(bookingRepository.findAllByItem_OwnerAndStartIsAfterOrderByStartDesc(any(), any(), any()))
                .thenReturn(List.of(booking3));
        BookingDtoList = (List<BookingDto>) bookingService
                .findBookingsByOwnerIdAndState(1L, "FUTURE", 0, 10);
        Assertions.assertFalse(BookingDtoList.isEmpty());

        start = LocalDateTime.now().minusDays(2L);
        end = LocalDateTime.now().minusDays(1L);
        Booking booking2 = Booking.builder()
                .id(2L)
                .start(start)
                .end(end)
                .item(item)
                .booker(booker)
                .status(Status.APPROVED)
                .build();
        when(bookingRepository.findAllByItem_OwnerAndEndIsBeforeOrderByStartDesc(any(), any(), any()))
                .thenReturn(List.of(booking2));
        BookingDtoList = (List<BookingDto>) bookingService
                .findBookingsByOwnerIdAndState(1L, "PAST", 0, 10);
        Assertions.assertFalse(BookingDtoList.isEmpty());

        start = LocalDateTime.now().plusDays(1L);
        end = LocalDateTime.now().plusDays(2L);
        Booking booking5 = Booking.builder()
                .id(5L)
                .start(start)
                .end(end)
                .item(item)
                .booker(booker)
                .status(Status.APPROVED)
                .build();

        when(bookingRepository.findAllByItem_OwnerAndStatusOrderByStartDesc(any(Long.class), any(Status.class), any(PageRequest.class)))
                .thenAnswer(
                        invocation -> {
                            Long userId = invocation.getArgument(0, Long.class);
                            Status status = invocation.getArgument(1, Status.class);
                            if (status.equals(Status.WAITING) && userId.equals(1L)) {
                                booking5.setStatus(Status.WAITING);
                                return List.of(booking5);
                            }
                            if (status.equals(Status.REJECTED) && userId.equals(1L)) {
                                booking5.setStatus(Status.REJECTED);
                                return List.of(booking5);
                            }
                            return Collections.emptyList();
                        }

                );

        User booker6 = User.builder()
                .id(6L)
                .name("user6")
                .email("user6@email.com")
                .build();
        when(userRepository.findById(6L))
                .thenReturn(Optional.of(booker6));

        BookingDtoList = (List<BookingDto>) bookingService
                .findBookingsByOwnerIdAndState(1L, "WAITING", 0, 10);
        Assertions.assertFalse(BookingDtoList.isEmpty());
        assertThat(BookingDtoList.get(0).getStatus(), is(Status.WAITING));

        BookingDtoList = (List<BookingDto>) bookingService
                .findBookingsByOwnerIdAndState(1L, "REJECTED", 0, 10);
        Assertions.assertFalse(BookingDtoList.isEmpty());
        assertThat(BookingDtoList.get(0).getStatus(), is(Status.REJECTED));

        BookingDtoList = (List<BookingDto>) bookingService
                .findBookingsByOwnerIdAndState(6L, "WAITING", 0, 10);
        Assertions.assertTrue(BookingDtoList.isEmpty());
    }
}