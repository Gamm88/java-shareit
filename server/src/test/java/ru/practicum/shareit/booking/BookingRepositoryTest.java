package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.item.Item;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;

@DataJpaTest
public class BookingRepositoryTest {
    @Autowired
    BookingRepository bookingRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ItemRepository itemRepository;

    @Test
    void testFindByBooker_Id() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        Item item = Item.builder()
                .name("item")
                .description("description")
                .available(true)
                .build();
        itemRepository.save(item);

        User user = User.builder()
                .name("user")
                .email("user@mail.com")
                .build();
        userRepository.save(user);

        Booking booking = Booking.builder()
                .start(start)
                .end(end)
                .item(item)
                .booker(user)
                .status(Status.WAITING)
                .build();
        bookingRepository.save(booking);

        List<Booking> bookings = (List<Booking>) bookingRepository.findByBooker_Id(1L);
        Assertions.assertFalse(bookings.isEmpty());
    }
}
