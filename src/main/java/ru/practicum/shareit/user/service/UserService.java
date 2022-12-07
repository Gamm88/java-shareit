package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserDto;

import java.util.Collection;

public interface UserService {
    // создать пользователя
    UserDto addUser(UserDto userDto);

    // получить всех пользователей
    Collection<UserDto> getUsers();

    // получить пользователя по ИД
    UserDto getUser(Long userId);

    // обновление пользователя
    UserDto updateUser(Long userId, UserDto userDto);

    // удалить пользователя по ИД
    void deleteUser(Long userId);

    // получение пользователя, если не найден - ошибка 404
    User getUserOrNotFound(Long userId);
}
