package com.example.CafeManagerCore.service;

import com.example.CafeManagerCore.DTO.RoleDTO;
import com.example.CafeManagerCore.DTO.RolePermissionAssignmentDTO;
import com.example.CafeManagerCore.model.RoleDB;

import java.util.List;

public interface RoleService {
    List<RoleDB> getAllRoles();
    RoleDB getRoleById(Long roleId);
    RoleDB getRoleByName(String roleName);
    List<RoleDB> getSubordinateRoles(RoleDB parentRole);
    RoleDB addRole(RoleDTO roleDTO);
    void assignPermissionToRole(RolePermissionAssignmentDTO assignmentDTO);
}
