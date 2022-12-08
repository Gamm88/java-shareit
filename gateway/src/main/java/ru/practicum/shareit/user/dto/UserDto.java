package ru.practicum.shareit.user.dto;


import lombok.*;

import javax.validation.constraints.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;

    @NotBlank(message = "Имя пользователя не может быть пустым")
    @Size(max = 30, message = "Максимальная длина имени пользователя — 30 символов")
    @Pattern(regexp = "\\S+", message = "Имя пользователя не может содержать пробелы")
    private String name;

    @Email(message = "Электронная почта указан некорректно")
    @NotBlank(message = "Электронная почта не может быть пустой")
    @Size(max = 50, message = "Максимальная длина электронной почты — 50 символов")
    private String email;
}