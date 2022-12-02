package ru.practicum.shareit.booking;

import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.model.Booking;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Status;

import java.util.Collection;
import java.time.LocalDateTime;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    /**
     * Для сервиса комментариев
     */
    Collection<Booking> findByBooker_Id(Long userId);

    /**
     * Для сервиса вещей
     */
    Collection<Booking> findByItem_IdAndItem_Owner(Long itemId, Long userId);

    Optional<Booking> findTop1BookingByItem_IdAndEndIsAfterAndStatusIsOrderByEndAsc(
            Long itemId, LocalDateTime end, Status status);

    Optional<Booking> findTop1BookingByItem_IdAndEndIsBeforeAndStatusIsOrderByEndDesc(
            Long itemId, LocalDateTime end, Status status);

    /**
     * Для сервиса аренды, поиск по арендатору вещи
     */

    Collection<Booking> findAllByBooker_IdOrderByStartDesc
            (Long userId, PageRequest pageRequest);

    @Query(value = "" +
            "SELECT * FROM bookings AS b " +
            "JOIN items i on i.id = b.item_id " +
            "WHERE b.booker_id= ?1 " +
            "AND (?2 between start_date and end_date)"
            , nativeQuery = true)
    Collection<Booking> findAllByBooker_IdAndStatusCurrentOrderByStartDesc
            (Long userId, LocalDateTime now, PageRequest pageRequest);

    Collection<Booking> findAllByBooker_IdAndStartIsAfterOrderByStartDesc
            (Long userId, LocalDateTime now, PageRequest pageRequest);

    Collection<Booking> findAllByBooker_IdAndEndIsBeforeOrderByStartDesc
            (Long userId, LocalDateTime now, PageRequest pageRequest);

    Collection<Booking> findAllByBooker_IdAndStatusOrderByStartDesc
            (Long userId, Status status, PageRequest pageRequest);

    /**
     * Для сервиса аренды, поиск по собственнику вещи
     */
    Collection<Booking> findAllByItem_OwnerOrderByStartDesc
            (Long ownerId, PageRequest pageRequest);

    @Query(value = "" +
            "SELECT * FROM bookings AS b " +
            "JOIN items i on i.id = b.item_id " +
            "WHERE i.owner_id= ?1 " +
            "AND (?2 between start_date and end_date)"
            , nativeQuery = true)
    Collection<Booking> findAllByItem_OwnerAndStatusCurrentOrderByStartDesc
            (Long ownerId, LocalDateTime now, PageRequest pageRequest);

    Collection<Booking> findAllByItem_OwnerAndStartIsAfterOrderByStartDesc
            (Long ownerId, LocalDateTime now, PageRequest pageRequest);

    Collection<Booking> findAllByItem_OwnerAndEndIsBeforeOrderByStartDesc
            (Long ownerId, LocalDateTime now, PageRequest pageRequest);

    Collection<Booking> findAllByItem_OwnerAndStatusOrderByStartDesc
            (Long ownerId, Status status, PageRequest pageRequest);
}