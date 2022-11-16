package ru.practicum.shareit.booking.model;

import lombok.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "bookings")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id; // ид бронирования
    @Column(name = "start_date")
    private Date startDate; // дата начала аренды
    @Column(name = "end_date")
    private Date endDate; // дата окончания аренды
    @Enumerated(EnumType.STRING)
    private Status status; // статус подтверждения аренды
    @ManyToOne
    @JoinColumn(name = "item_id", referencedColumnName = "id")
    private Item item; // арендуемая вещь
    @ManyToOne
    @JoinColumn(name = "booker_id", referencedColumnName = "id")
    private User booker; // пользователь арендующий вещь
}
