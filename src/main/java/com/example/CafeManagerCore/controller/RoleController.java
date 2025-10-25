package com.example.CafeManagerCore.controller;

import com.example.CafeManagerCore.DTO.RoleDTO;
import com.example.CafeManagerCore.DTO.RolePermissionAssignmentDTO;
import com.example.CafeManagerCore.model.RoleDB;
import com.example.CafeManagerCore.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/roles")
@Tag(name = "Роли", description = "Методы управления ролями пользователей и правами")
public class RoleController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    @Autowired
    final private RoleService roleService;

    @Operation(summary = "Получить все роли", description = "Требуются права CRUD_ROLES")
    @ApiResponse(responseCode = "200", description = "Список ролей успешно получен")
    @PreAuthorize("hasAuthority('CRUD_ROLES')")
    @GetMapping
    public ResponseEntity<List<RoleDB>> getAllRoles() {
        List<RoleDB> roles = roleService.getAllRoles();
        return ResponseEntity.ok(roles);
    }

    @Operation(summary = "Создать новую роль", description = "Создаёт роль. Требуются права CRUD_ROLES")
    @ApiResponse(responseCode = "200", description = "Роль успешно создана")
    @PreAuthorize("hasAuthority('CRUD_ROLES')")
    @PostMapping("/create")
    public ResponseEntity<RoleDB> addRole(@RequestBody RoleDTO roleDTO) {
        logger.info("Запрос на создание роли: {}", roleDTO);
        RoleDB role = roleService.addRole(roleDTO);
        logger.info("Роль создана успешно: {}", role);
        return ResponseEntity.ok(role);
    }

    @Operation(summary = "Получить роль по ID", description = "Возвращает роль по идентификатору. Требуются права CRUD_ROLES")
    @ApiResponse(responseCode = "200", description = "Роль найдена")
    @PreAuthorize("hasAuthority('CRUD_ROLES')")
    @GetMapping("/{roleId}")
    public ResponseEntity<RoleDB> getRoleById(@PathVariable Long roleId) {
        RoleDB role = roleService.getRoleById(roleId);
        return ResponseEntity.ok(role);
    }

    @Operation(summary = "Получить роль по названию", description = "Возвращает роль по имени. Требуются права CRUD_ROLES")
    @ApiResponse(responseCode = "200", description = "Роль найдена")
    @PreAuthorize("hasAuthority('CRUD_ROLES')")
    @GetMapping("/name/{roleName}")
    public ResponseEntity<RoleDB> getRoleByName(@PathVariable String roleName) {
        RoleDB role = roleService.getRoleByName(roleName);
        return ResponseEntity.ok(role);
    }

    @Operation(summary = "Получить подчинённые роли", description = "Возвращает список ролей, подчинённых указанной. Требуются права CRUD_ROLES")
    @ApiResponse(responseCode = "200", description = "Список подчинённых ролей получен")
    @PreAuthorize("hasAuthority('CRUD_ROLES')")
    @GetMapping("/subordinate/{parentRoleId}")
    public ResponseEntity<List<RoleDB>> getSubordinateRoles(@PathVariable Long parentRoleId) {
        RoleDB parentRole = roleService.getRoleById(parentRoleId);
        List<RoleDB> subordinateRoles = roleService.getSubordinateRoles(parentRole);
        return ResponseEntity.ok(subordinateRoles);
    }

    @Operation(summary = "Назначить право роли", description = "Назначает право доступа указанной роли. Требуются права ASSIGN_PERMISSION")
    @ApiResponse(responseCode = "200", description = "Право назначено роли")
    @PreAuthorize("hasAuthority('ASSIGN_PERMISSION')")
    @PostMapping("/assign-permission")
    public ResponseEntity<Void> assignPermissionToRole(@RequestBody RolePermissionAssignmentDTO assignmentDTO) {
        roleService.assignPermissionToRole(assignmentDTO);
        return ResponseEntity.ok().build();
    }
}
