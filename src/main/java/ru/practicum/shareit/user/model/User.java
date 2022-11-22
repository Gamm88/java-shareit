package ru.practicum.shareit.user.model;

import lombok.*;

import javax.persistence.*;

/**
 * Модель пользователя
 */
@Entity
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // ид пользователя

    @Column(name = "name")
    private String name; // имя пользователя

    @Column(name = "email", unique = true)
    private String email; // почта пользователя

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        return id != null && id.equals(((User) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
