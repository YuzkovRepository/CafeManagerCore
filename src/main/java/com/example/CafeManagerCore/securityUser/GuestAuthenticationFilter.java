package com.example.CafeManagerCore.securityUser;

import com.example.CafeManagerCore.exception.CommonException;
import com.example.CafeManagerCore.model.Guest;
import com.example.CafeManagerCore.service.GuestService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class GuestAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private GuestService guestService;

    private static final Logger logger = LoggerFactory.getLogger(GuestAuthenticationFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String phone = extractPhoneFromRequest(request);
        logger.debug("Guest filter checking phone: {}", phone);

        if (phone != null) {
            try {
                Guest guest = guestService.findOrCreateGuest(phone);
                GuestAuthenticationToken auth = new GuestAuthenticationToken(phone, guest);
                SecurityContextHolder.getContext().setAuthentication(auth);
                logger.info("✅ Guest authentication set for phone: {} (Guest ID: {})", phone, guest.getId());
            } catch (Exception e) {
                logger.error("Failed to authenticate guest with phone: {}", phone, e);
            }
        }

        filterChain.doFilter(request, response);
    }

    private String extractPhoneFromRequest(HttpServletRequest request) {
        // 1. Проверяем header
        String phone = request.getHeader("X-Guest-Phone");
        if (phone != null && isValidPhone(phone)) {
            logger.debug("Found phone in header: {}", phone);
            return phone;
        }

        // 2. Проверяем query parameter
        phone = request.getParameter("guestPhone");
        if (phone != null && isValidPhone(phone)) {
            logger.debug("Found phone in parameter: {}", phone);
            return phone;
        }

        // 3. Проверяем путь URL
        String path = request.getRequestURI();
        logger.debug("Checking path for phone: {}", path);

        if ((path.startsWith("/api/v1/carts/") || path.startsWith("/api/v1/orders/")) &&
                !path.contains("/admin/") && !path.contains("/management/")) {

            // Извлекаем phone из пути: /api/v1/carts/add/+79116239444
            String[] pathParts = path.split("/");
            for (int i = 0; i < pathParts.length; i++) {
                if (pathParts[i].startsWith("+") && isValidPhone(pathParts[i])) {
                    logger.debug("Found phone in path: {}", pathParts[i]);
                    return pathParts[i];
                }
            }
        }

        return null;
    }

    private boolean isValidPhone(String phone) {
        if (phone == null) return false;
        boolean valid = phone.matches("^\\+?[7-8]?[0-9]{10}$");
        logger.debug("Phone validation for '{}': {}", phone, valid);
        return valid;
    }
}