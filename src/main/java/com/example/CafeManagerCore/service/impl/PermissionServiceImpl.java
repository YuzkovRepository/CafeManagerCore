package com.example.CafeManagerCore.service.impl;

import com.example.CafeManagerCore.DTO.PermissionDTO;
import com.example.CafeManagerCore.model.Permission;
import com.example.CafeManagerCore.repository.PermissionRepository;
import com.example.CafeManagerCore.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {
    private final PermissionRepository permissionRepository;

    @Override
    public List<Permission> getAllPermissions() {
        return permissionRepository.findAll();
    }

    @Override
    public Permission getPermissionById(Long permissionId) {
        return permissionRepository.findById(permissionId).orElse(null);
    }

    @Override
    public Permission getPermissionByName(String permissionName) {
        return permissionRepository.findByPermissionName(permissionName).orElse(null);
    }

    @Override
    public Permission addPermission(PermissionDTO permissionDTO) {
        Permission permission = new Permission(permissionDTO.permissionName(), permissionDTO.description());
        return permissionRepository.save(permission);
    }
}