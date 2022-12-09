package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.user.dto.UserDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserClient userClient;

    // создать пользователя
    @PostMapping
    public ResponseEntity<Object> addUser(@Valid @RequestBody UserDto userDto) {
        log.info("UserController - создание пользователя: {}", userDto);

        return userClient.addUser(userDto);
    }

    // получить всех пользователей
    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        log.info("UserController - получение всех пользователей");

        return userClient.getUsers();
    }

    // получить пользователя по ИД
    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUser(@PathVariable("userId") Long userId) {
        log.info("UserController - получение пользователя по ИД: {}", userId);

        return userClient.getUser(userId);
    }

    // обновить пользователя по ИД
    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@PathVariable("userId") Long userId,
                              @RequestBody UserDto userDto) {
        log.info("UserController - обновление пользователя с ИД: {}, новое значение: {}", userId, userDto);

        return userClient.updateUser(userId, userDto);
    }

    // удалить пользователя по ИД
    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable("userId") Long userId) {
        log.info("UserController - удаление пользователя по ИД: {}", userId);
        userClient.deleteUser(userId);
    }
}