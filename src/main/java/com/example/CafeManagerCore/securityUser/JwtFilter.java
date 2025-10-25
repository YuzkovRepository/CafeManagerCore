package com.example.CafeManagerCore.securityUser;

import com.example.CafeManagerCore.model.Guest;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class);

    public JwtFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        Authentication existingAuth = SecurityContextHolder.getContext().getAuthentication();
        if (existingAuth != null && existingAuth.isAuthenticated()) {
            String authType = existingAuth.getClass().getSimpleName();
            logger.debug("Already authenticated as {}: {}", authType, getAuthName(existingAuth));
            filterChain.doFilter(request, response);
            return;
        }

        String token = jwtUtil.getTokenFromRequest(request);
        if (token != null) {
            if (jwtUtil.isValidToken(token)) {
                Authentication authentication = jwtUtil.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                logger.info("JWT Authentication set for user: {}", authentication.getName());
            } else {
                logger.warn("Invalid JWT token");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Invalid token");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private String getAuthName(Authentication auth) {
        if (auth instanceof GuestAuthenticationToken) {
            Guest guest = ((GuestAuthenticationToken) auth).getGuest();
            return "Guest#" + guest.getId() + " (" + guest.getPhone() + ")";
        }
        return auth.getName();
    }
}
