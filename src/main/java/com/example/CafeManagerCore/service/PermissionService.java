package com.example.CafeManagerCore.service;

import com.example.CafeManagerCore.DTO.PermissionDTO;
import com.example.CafeManagerCore.model.Permission;

import java.util.List;

public interface PermissionService {
    List<Permission> getAllPermissions();
    Permission getPermissionById(Long permissionId);
    Permission getPermissionByName(String permissionName);
    Permission addPermission(PermissionDTO permissionDTO);
}
