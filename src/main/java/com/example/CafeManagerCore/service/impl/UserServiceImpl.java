package com.example.CafeManagerCore.service.impl;

import com.example.CafeManagerCore.exception.CommonException;
import com.example.CafeManagerCore.DTO.*;
import com.example.CafeManagerCore.model.*;
import com.example.CafeManagerCore.repository.RoleRepository;
import com.example.CafeManagerCore.repository.UserRepository;
import com.example.CafeManagerCore.repository.UserRoleRepository;
import com.example.CafeManagerCore.securityUser.JwtUtil;
import com.example.CafeManagerCore.securityUser.Validators.ValidatorUser;
import com.example.CafeManagerCore.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final ValidatorUser validatorUser;

    @Override
    public UserResponseDTO registerUser(UserRequestDTO requestDTO) {
        validatorUser.validateRegisterRequest(requestDTO);

        if (userRepository.existsByLogin(requestDTO.login())) {
            throw new CommonException("Логин уже существует");
        }

        User user = new User();
        user.setLogin(requestDTO.login());
        user.setPassword_hash(passwordEncoder.encode(requestDTO.password()));
        user.setEmail(requestDTO.email());
        user.setPhone(requestDTO.phone());
        user.setFirstname(requestDTO.firstname());
        user.setLastname(requestDTO.lastname());
        user.setSurname(requestDTO.surname());
        User savedUser = userRepository.save(user);

        RoleDB roleDB = roleRepository.findById(2L)
                .orElseThrow(() -> new CommonException("Роль пользователя не найдена"));
        UserRole userRole = new UserRole();
        userRole.setUser(savedUser);
        userRole.setRole(roleDB);
        userRoleRepository.save(userRole);

        logger.info("Пользователь успешно зарегистрировался с ролью: {}", savedUser.getLogin());

        return new UserResponseDTO(
                savedUser.getLogin(),
                savedUser.getPassword_hash(),
                savedUser.getEmail(),
                savedUser.getPhone(),
                savedUser.getStatus().name()
        );
    }

    @Transactional
    @Override
    public String authenticate(UserLoginRequestDTO requestDTO) {
        logger.info("Получен запрос на аутентификацию для: {}", requestDTO.loginOrEmail());
        validatorUser.validateLoginRequest(requestDTO);

        String loginOrEmail = requestDTO.loginOrEmail();
        String password = requestDTO.password();

        User user;
        if (loginOrEmail.contains("@")) {
            user = userRepository.findByEmail(loginOrEmail)
                    .orElseThrow(() -> new CommonException("Пользователь не найден"));
        } else {
            user = userRepository.findByLogin(loginOrEmail)
                    .orElseThrow(() -> new CommonException("Пользователь не найден"));
        }
        logger.info(user.getUserRoles().toString());

        if (!passwordEncoder.matches(password, user.getPassword_hash())) {
            logger.error("Invalid password for the user: {}", loginOrEmail);
            throw new CommonException("Invalid password");
        }

        List<UserRole> userRoles = userRoleRepository.findWithRolesByUser(user);

        List<String> roles = userRoles.stream()
                .map(ur -> ur.getRole().getRoleName())
                .collect(Collectors.toList());

        String token = jwtUtil.generateToken(user.getLogin(), roles);
        logger.info("Generated a JWT token for the user: {}", user.getLogin());
        return token;
    }

    @Override
    public List<RoleResponseDTO> getUserRoles(Long userId) {
        try {
            List<RoleDB> roles = userRoleRepository.findRolesByUserId(userId);

            return roles.stream()
                    .map(role -> new RoleResponseDTO(role.getRoleId(), role.getRoleName()))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error when selecting roles for a user with an ID: {}", userId, e);
            throw e;
        }
    }

    @Override
    public List<PermissionResponseDTO> getUserPermissions(Long userId) {
        try {
            List<Permission> permissions = userRoleRepository.findPermissionsByUserId(userId);

            List<PermissionResponseDTO> permissionDTOs = permissions.stream()
                    .map(permission -> new PermissionResponseDTO(
                            permission.getPermissionsId(),
                            permission.getPermissionName(),
                            permission.getDescription()
                    ))
                    .collect(Collectors.toList());

            return permissionDTOs;
        } catch (Exception e) {
            logger.error("Error when getting permissions for a user with an ID: {}", userId, e);
            throw e;
        }
    }

    @Override
    public void assignRoleToUser(UserRoleAssignmentDTO assignmentDTO) {
        User user = userRepository.findById(assignmentDTO.userId())
                .orElseThrow(() -> new UsernameNotFoundException("The user was not found"));
        RoleDB role = roleRepository.findById(assignmentDTO.roleId())
                .orElseThrow(() -> new CommonException("Role not found"));

        UserRole userRole = new UserRole();
        userRole.setUser(user);
        userRole.setRole(role);

        userRoleRepository.save(userRole);
    }

    @Override
    public List<RolePermissionDTO> getRolesAndPermissionsByLogin(String login) {
        try {
            return userRepository.findRolesAndPermissionsByLogin(login);
        } catch (Exception e) {
            logger.error("Error when selecting roles and permissions to log in: {}", login, e);
            throw new CommonException("Roles and permissions could not be obtained");
        }
    }
}
