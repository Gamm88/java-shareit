package ru.practicum.shareit.booking.model;

import lombok.*;
import ru.practicum.shareit.item.model.item.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "bookings")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // ид бронирования
    @Column(name = "start_date")
    private LocalDateTime start; // дата начала аренды
    @Column(name = "end_date")
    private LocalDateTime  end; // дата окончания аренды
    @ManyToOne
    @JoinColumn(name = "item_id", referencedColumnName = "id")
    private Item item; // арендуемая вещь
    @ManyToOne
    @JoinColumn(name = "booker_id", referencedColumnName = "id")
    private User booker; // пользователь арендующий вещь
    @Enumerated(EnumType.STRING)
    private Status status; // статус подтверждения аренды

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Booking)) return false;
        return id != null && id.equals(((Booking) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    public Booking(LocalDateTime  start, LocalDateTime  end, Item item, User booker) {
        this.start = start;
        this.end = end;
        this.status = Status.WAITING;
        this.item = item;
        this.booker = booker;
    }
}
