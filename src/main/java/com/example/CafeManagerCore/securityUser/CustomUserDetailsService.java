package com.example.CafeManagerCore.securityUser;

import com.example.CafeManagerCore.model.RoleDB;
import com.example.CafeManagerCore.model.User;
import com.example.CafeManagerCore.model.UserRole;
import com.example.CafeManagerCore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOptional = userRepository.findByLogin(username);
        User user = userOptional.orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));
        return new org.springframework.security.core.userdetails.User(
                user.getLogin(),
                user.getPassword_hash(),
                mapRolesToAuthorities(user.getUserRoles())
        );
    }

    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Set<UserRole> userRoles) {
        return userRoles.stream()
                .flatMap(userRole -> {
                    RoleDB role = userRole.getRole();
                    Stream<GrantedAuthority> roleAuthorities = Stream.of(new SimpleGrantedAuthority(role.getRoleName()));
                    Stream<GrantedAuthority> permissionAuthorities = role.getRolePermissions().stream()
                            .map(rolePermission -> new SimpleGrantedAuthority(rolePermission.getPermission().getPermissionName()));
                    return Stream.concat(roleAuthorities, permissionAuthorities);
                })
                .collect(Collectors.toList());
    }
}