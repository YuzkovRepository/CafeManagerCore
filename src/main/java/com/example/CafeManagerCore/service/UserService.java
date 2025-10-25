package com.example.CafeManagerCore.service;


import com.example.CafeManagerCore.DTO.*;

import java.util.List;

public interface UserService {
    UserResponseDTO registerUser(UserRequestDTO requestDTO);
    String authenticate(UserLoginRequestDTO requestDTO);
    List<RoleResponseDTO> getUserRoles(Long userId);
    List<PermissionResponseDTO> getUserPermissions(Long userId);
    void assignRoleToUser(UserRoleAssignmentDTO assignmentDTO);
    List<RolePermissionDTO> getRolesAndPermissionsByLogin(String login);
}
