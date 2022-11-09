package ru.practicum.shareit.user.servise;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.model.UserDto;
import ru.practicum.shareit.user.model.UserMapper;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.DuplicateException;

import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    // создать пользователя
    public UserDto createUser(UserDto userDto) {
        checkingForDuplicationEmail(userDto.getId(), userDto.getEmail());
        User createdUser = userRepository.createUser(UserMapper.toUser(userDto));
        log.info("UserService - в базу добавлен пользователь: {} ", createdUser);

        return UserMapper.toUserDto(createdUser);
    }

    // получить всех пользователей
    public Collection<UserDto> getAllUsers() {
        Collection<UserDto> usersDtoList = userRepository.getAllUsers().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
        log.info("UserService - предоставлен список пользователей: {} ", usersDtoList);

        return usersDtoList;
    }

    // получить пользователя по ИД
    public UserDto getUserById(Long userId) {
        User getUser = getUserOrNotFound(userId);
        UserDto userDto = UserMapper.toUserDto(getUser);
        log.info("UserService - по ИД: {} получен пользователь: {}", userId, userDto);

        return userDto;
    }

    // обновление пользователя
    public UserDto updateUser(Long userId, UserDto userDto) {
        User updatedUser = getUserOrNotFound(userId);

        String newEmail = userDto.getEmail();
        String newName = userDto.getName();

        if (newEmail != null) {
            checkingForDuplicationEmail(userId, newEmail);
            updatedUser.setEmail(newEmail);
        }
        if (newName != null) {
            updatedUser.setName(newName);
        }

        updatedUser = userRepository.updateUser(updatedUser);
        log.info("UserService - в базе обновлён пользователь: {}", updatedUser);

        return UserMapper.toUserDto(updatedUser);
    }

    // удалить пользователя по ИД
    public void deleteUserById(Long userId) {
        getUserOrNotFound(userId);
        log.info("UserController - удаление пользователя по ИД: {}", userId);
        userRepository.deleteUserById(userId);
    }

    // получение пользователя, если не найден - ошибка 404
    public User getUserOrNotFound(Long userId) {
        User user = userRepository.getUserById(userId);
        if (user == null) {
            throw new NotFoundException("Пользователь с ИД " + userId + " не найден.");
        }

        return user;
    }

    // проверка на дублирование пользователей, по почте
    private void checkingForDuplicationEmail(Long userId, String userEmail) {
        for (User user : userRepository.getAllUsers()) {
            if (user.getEmail().equals(userEmail) && user.getId() != userId) {
                throw new DuplicateException("Электронная почта "  + userEmail + " уже зарегистрирована.");
            }
        }
    }
}