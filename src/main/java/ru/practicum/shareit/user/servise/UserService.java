package ru.practicum.shareit.user.servise;

import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserDto;

import java.util.Collection;

public interface UserService {
    // создать пользователя
    UserDto createUser(UserDto userDto);

    // получить всех пользователей
    Collection<UserDto> getAllUsers();

    // получить пользователя по ИД
    UserDto getUserById(Long userId);

    // обновление пользователя
    UserDto updateUser(Long userId, UserDto userDto);

    // удалить пользователя по ИД
    void deleteUserById(Long userId);

    // получение пользователя, если не найден - ошибка 404
    User getUserOrNotFound(Long userId);
}
