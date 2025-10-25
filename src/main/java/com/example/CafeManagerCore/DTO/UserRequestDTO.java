package com.example.CafeManagerCore.DTO;

import jakarta.validation.constraints.*;

public record UserRequestDTO(
        @NotBlank(message = "Логин не может быть нулевым или пустым")
        @Size(min = 8, max = 20, message = "Длина логина должна составлять от 8 до 20 символов")
        String login,

        @NotBlank(message = "Пароль не может быть нулевым или незаполненным")
        @Size(min = 8, max = 20, message = "Пароль должен содержать от 8 до 20 символов")
        @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).*$", message = "Password must contain at least one digit, one lowercase letter, one uppercase letter, one special character, and no spaces")
        String password,

        @NotBlank(message = "Адрес электронной почты не может быть пустым")
        @Email(message = "Адрес электронной почты должен быть действительным")
        String email,

        @NotBlank(message = "Номер телефона не может быть нулевым или пустым")
        String phone,

        @NotBlank(message ="Имя не может быть пустым")
        String firstname,
        String lastname,
        @NotBlank(message ="Фамилия не может быть пустым")
        String surname
) {}