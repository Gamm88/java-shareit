package ru.practicum.shareit.item.model.item;

import lombok.*;
import ru.practicum.shareit.request.model.ItemRequest;

import javax.persistence.*;

/**
 * Модель вещи которая может быть арендована
 */
@Entity
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "items")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // ид вещи

    @Column(name = "owner_id")
    private Long owner; // ид владельца вещи

    @Column
    private String name; // название вещи

    @Column
    private String description; // описание вещи

    @Column
    private Boolean available; // доступность вещи для аренды, проставлять владелец, по умолчанию true.

    @ManyToOne
    @JoinColumn(name = "request_id")
    private ItemRequest request;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Item)) return false;
        return id != null && id.equals(((Item) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}