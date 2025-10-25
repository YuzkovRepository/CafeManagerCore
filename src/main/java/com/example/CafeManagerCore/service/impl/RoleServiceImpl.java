package com.example.CafeManagerCore.service.impl;

import com.example.CafeManagerCore.exception.CommonException;
import com.example.CafeManagerCore.controller.UserController;
import com.example.CafeManagerCore.DTO.RoleDTO;
import com.example.CafeManagerCore.DTO.RolePermissionAssignmentDTO;
import com.example.CafeManagerCore.model.Permission;
import com.example.CafeManagerCore.model.RoleDB;
import com.example.CafeManagerCore.model.RolePermission;
import com.example.CafeManagerCore.repository.PermissionRepository;
import com.example.CafeManagerCore.repository.RolePermissionRepository;
import com.example.CafeManagerCore.repository.RoleRepository;
import com.example.CafeManagerCore.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final RolePermissionRepository rolePermissionRepository;

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Override
    public List<RoleDB> getAllRoles() {
        return roleRepository.findAll();
    }

    @Override
    public RoleDB getRoleById(Long roleId) {
        return roleRepository.findById(roleId)
                .orElseThrow(() -> new UsernameNotFoundException("Роль с идентификатором не найдена: " + roleId));
    }

    @Override
    public RoleDB getRoleByName(String roleName) {
        return roleRepository.findByRoleName(roleName);
    }

    @Override
    public List<RoleDB> getSubordinateRoles(RoleDB parentRole) {
        return roleRepository.findByParentRole(parentRole);
    }

    @Override
    public RoleDB addRole(RoleDTO roleDTO) {
        logger.info("Добавление роли: {}", roleDTO);

        RoleDB role;

        if (roleDTO.parentRoleId() != null) {
            logger.info("Ищем родительскую роль с идентификатором: {}", roleDTO.parentRoleId());
            RoleDB parentRole = roleRepository.findById(roleDTO.parentRoleId())
                    .orElseThrow(() -> new CommonException("Родительская роль не найдена с идентификатором: " + roleDTO.parentRoleId()));
            role = new RoleDB(roleDTO.roleName(), roleDTO.description(), parentRole);
        } else {
            role = new RoleDB(roleDTO.roleName(), roleDTO.description());
        }

        RoleDB savedRole = roleRepository.save(role);
        logger.info("Роль успешно сохранена: {}", savedRole);
        return savedRole;
    }

    @Override
    public void assignPermissionToRole(RolePermissionAssignmentDTO assignmentDTO) {
        RoleDB role = roleRepository.findById(assignmentDTO.roleId())
                .orElseThrow(() -> new UsernameNotFoundException("Роль с идентификатором не найдена: " + assignmentDTO.roleId()));
        Permission permission = permissionRepository.findById(assignmentDTO.permissionId())
                .orElseThrow(() -> new CommonException("Разрешение не найдено с идентификатором: " + assignmentDTO.permissionId()));

        RolePermission rolePermission = new RolePermission(role, permission);
        rolePermissionRepository.save(rolePermission);
    }
}

