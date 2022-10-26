package ru.practicum.shareit.user;

import ru.practicum.shareit.user.model.User;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class UserRepository {
    private long id = 0; // уникальный ID пользователя
    private final Map<Long, User> users = new HashMap<>(); // хранение пользователей

    // создать пользователя
    public User createUser(User user) {
        user.setId(++id);
        users.put(user.getId(), user);

        return user;
    }

    // получить всех пользователей
    public Collection<User> getAllUsers() {

        return users.values();
    }

    // получить пользователя по ИД
    public User getUserById(Long userId) {

        return users.get(userId);
    }

    // обновление пользователя
    public User updateUser(User user) {
        users.put(user.getId(), user);

        return users.get(user.getId());
    }

    // удалить пользователя по ИД
    public void deleteUserById(Long userId) {
        users.remove(userId);
    }
}