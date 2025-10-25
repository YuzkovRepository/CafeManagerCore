package com.example.CafeManagerCore.securityUser.Validators;

import com.example.CafeManagerCore.DTO.UserLoginRequestDTO;
import com.example.CafeManagerCore.DTO.UserRequestDTO;
import org.springframework.stereotype.Component;

@Component
public class ValidatorUser {
    public void validateRegisterRequest(UserRequestDTO requestDTO) {
        if (requestDTO.login() == null || requestDTO.login().isEmpty()) {
            throw new IllegalArgumentException("Логин не может быть нулевым или пустым");
        }
        if (requestDTO.password() == null || requestDTO.password().isEmpty()) {
            throw new IllegalArgumentException("Пароль не может быть нулевым или пустым");
        }
        if (requestDTO.email() == null || requestDTO.email().isEmpty()) {
            throw new IllegalArgumentException("Пароль не может быть нулевым или пустым");
        }
        if (requestDTO.phone() == null || requestDTO.phone().isEmpty()) {
            throw new IllegalArgumentException("Номер телефона не может быть нулевым или пустым");
        }
    }

    public void validateLoginRequest(UserLoginRequestDTO requestDTO) {
        if (requestDTO.loginOrEmail() == null || requestDTO.loginOrEmail().isEmpty()) {
            throw new IllegalArgumentException("Логин или адрес электронной почты не могут быть пустыми");
        }
        if (requestDTO.password() == null || requestDTO.password().isEmpty()) {
            throw new IllegalArgumentException("Пароль не может быть нулевым или незаполненным");
        }
    }
}
