package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.user.model.UserDto;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.servise.UserServiceImpl;

import java.util.Collection;
import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserServiceImpl userService;

    // создать пользователя
    @PostMapping
    public UserDto createUser(@Valid @RequestBody UserDto userDto) {
        log.info("UserController - создание пользователя: {}", userDto);

        return userService.createUser(userDto);
    }

    // получить всех пользователей
    @GetMapping
    public Collection<UserDto> getAllUsers() {
        log.info("UserController - получение всех пользователей");

        return userService.getAllUsers();
    }

    // получить пользователя по ИД
    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable("userId") Long userId) {
        log.info("UserController - получение пользователя по ИД: {}", userId);

        return userService.getUserById(userId);
    }

    // обновить пользователя по ИД
    @PatchMapping("/{userId}")
    public UserDto updateUser(@PathVariable("userId") Long userId,
                              @RequestBody UserDto userDto) {
        log.info("UserController - обновление пользователя с ИД: {}, новое значение: {}", userId, userDto);

        return userService.updateUser(userId, userDto);
    }

    // удалить пользователя по ИД
    @DeleteMapping("/{userId}")
    public void deleteUserById(@PathVariable("userId") Long userId) {
        log.info("UserController - удаление пользователя по ИД: {}", userId);
        userService.deleteUserById(userId);
    }
}
