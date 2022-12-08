package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.user.model.UserDto;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collection;
import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    // создать пользователя
    @PostMapping
    public UserDto addUser(@Valid @RequestBody UserDto userDto) {
        log.info("UserController - создание пользователя: {}", userDto);

        return userService.addUser(userDto);
    }

    // получить всех пользователей
    @GetMapping
    public Collection<UserDto> getAllUsers() {
        log.info("UserController - получение всех пользователей");

        return userService.getUsers();
    }

    // получить пользователя по ИД
    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable("userId") Long userId) {
        log.info("UserController - получение пользователя по ИД: {}", userId);

        return userService.getUser(userId);
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
    public void deleteUser(@PathVariable("userId") Long userId) {
        log.info("UserController - удаление пользователя по ИД: {}", userId);
        userService.deleteUser(userId);
    }
}