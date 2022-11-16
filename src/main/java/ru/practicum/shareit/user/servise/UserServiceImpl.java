package ru.practicum.shareit.user.servise;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.user.model.User;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.model.UserDto;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.UserMapper;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    // создать пользователя
    public UserDto addUser(UserDto userDto) {
        User createdUser = userRepository.save(UserMapper.mapToUser(userDto));
        log.info("UserService - в базу добавлен пользователь: {} ", createdUser);

        return UserMapper.mapToUserDto(createdUser);
    }

    // получить всех пользователей
    public Collection<UserDto> getUsers() {
        Collection<UserDto> usersDtoList = UserMapper.mapToUserDto(userRepository.findAll());
        log.info("UserService - предоставлен список пользователей: {} ", usersDtoList);

        return usersDtoList;
    }

    // получить пользователя по ИД
    public UserDto getUser(Long userId) {
        User getUser = getUserOrNotFound(userId);
        UserDto userDto = UserMapper.mapToUserDto(getUser);
        log.info("UserService - по ИД: {} получен пользователь: {}", userId, userDto);

        return userDto;
    }

    // обновление пользователя
    public UserDto updateUser(Long userId, UserDto userDto) {
        User updatedUser = getUserOrNotFound(userId);

        String newEmail = userDto.getEmail();
        String newName = userDto.getName();

        if (newEmail != null) {
            updatedUser.setEmail(newEmail);
        }
        if (newName != null) {
            updatedUser.setName(newName);
        }

        updatedUser = userRepository.save(updatedUser);
        log.info("UserService - в базе обновлён пользователь: {}", updatedUser);

        return UserMapper.mapToUserDto(updatedUser);
    }

    // удалить пользователя по ИД
    public void deleteUser(Long userId) {
        getUserOrNotFound(userId);
        log.info("UserController - удаление пользователя по ИД: {}", userId);
        userRepository.deleteById(userId);
    }

    // получение пользователя, если не найден - ошибка 404
    public User getUserOrNotFound(Long userId) {
        return userRepository
                .findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ИД " + userId + " не найден."));
    }
}