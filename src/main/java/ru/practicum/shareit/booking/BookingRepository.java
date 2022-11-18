package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.model.Booking;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Status;

import java.util.Collection;
import java.time.LocalDateTime;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    /**
     * Для сервиса комментариев
     */
    Collection<Booking> findByBooker_Id(Long userId);

    /**
     * Для сервиса вещей
     */
    Collection<Booking> findByItem_IdAndItem_Owner(Long itemId, Long userId);

    Collection<Booking> findByItem_Id(Long itemId);

    /**
     * По арендатору вещи
     */
    Collection<Booking> findAllByBooker_IdOrderByStartDesc(Long userId);

    @Query(value = "" +
            "SELECT * FROM bookings AS b " +
            "JOIN items i on i.id = b.item_id " +
            "WHERE b.booker_id= ?1 " +
            "AND (?2 between start_date and end_date)"
            , nativeQuery = true)
    Collection<Booking> findAllByBooker_IdAndStatusCurrentOrderByStartDesc(Long userId, LocalDateTime now);

    Collection<Booking> findAllByBooker_IdAndStartIsAfterOrderByStartDesc(Long userId, LocalDateTime now);

    Collection<Booking> findAllByBooker_IdAndEndIsBeforeOrderByStartDesc(Long userId, LocalDateTime now);

    Collection<Booking> findAllByBooker_IdAndStatusOrderByStartDesc(Long userId, Status status);

    /**
     * По собственнику вещи
     */
    Collection<Booking> findAllByItem_OwnerOrderByStartDesc(Long ownerId);

    @Query(value = "" +
            "SELECT * FROM bookings AS b " +
            "JOIN items i on i.id = b.item_id " +
            "WHERE i.owner_id= ?1 " +
            "AND (?2 between start_date and end_date)"
            , nativeQuery = true)
    Collection<Booking> findAllByItem_OwnerAndStatusCurrentOrderByStartDesc(Long ownerId, LocalDateTime now);

    Collection<Booking> findAllByItem_OwnerAndStartIsAfterOrderByStartDesc(Long ownerId, LocalDateTime now);

    Collection<Booking> findAllByItem_OwnerAndEndIsBeforeOrderByStartDesc(Long ownerId, LocalDateTime now);

    Collection<Booking> findAllByItem_OwnerAndStatusOrderByStartDesc(Long ownerId, Status status);
}