package com.military.asset.security;

import com.military.asset.service.AuditLogService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final AuditLogService auditLogService;

    public CustomAccessDeniedHandler(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException)
            throws IOException, ServletException {
        
        auditLogService.logAction(
                "SECURITY_ACCESS_DENIED",
                request.getRequestURI(),
                "Access Denied for user on " + request.getMethod() + " " + request.getRequestURI() + ": " + accessDeniedException.getMessage(),
                null,
                403
        );

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"Forbidden\", \"message\": \"Access Denied: You do not have permission to perform this operation.\"}");
    }
}
