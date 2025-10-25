package com.example.CafeManagerCore.securityUser;

import com.example.CafeManagerCore.DTO.RolePermissionDTO;
import com.example.CafeManagerCore.service.UserService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


@Component
public class JwtUtil {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    @Autowired
    @Lazy
    private UserService userService;

    private final SecretKey SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    public String generateToken(String login, List<String> roles) {
        return Jwts.builder()
                .setSubject(login)
                .claim("roles", roles)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    public String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public boolean isValidToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(SECRET_KEY).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String extractLogin(String token) {
        return Jwts.parserBuilder().setSigningKey(SECRET_KEY).build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public Authentication getAuthentication(String token) {
        String login = extractLogin(token);

        List<RolePermissionDTO> rolesAndPermissions = userService.getRolesAndPermissionsByLogin(login);

        List<GrantedAuthority> authorities = rolesAndPermissions.stream()
                .map(rp -> new SimpleGrantedAuthority(rp.permissionName()))
                .collect(Collectors.toList());

        logger.info("Предоставленные полномочия: {}", authorities);

        return new UsernamePasswordAuthenticationToken(login, null, authorities);
    }
}