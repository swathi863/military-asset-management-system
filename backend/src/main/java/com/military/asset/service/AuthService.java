package com.military.asset.service;

import com.military.asset.dto.AuthRequest;
import com.military.asset.dto.AuthResponse;
import com.military.asset.security.JwtTokenProvider;
import com.military.asset.security.UserPrincipal;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final AuditLogService auditLogService;

    public AuthService(AuthenticationManager authenticationManager, JwtTokenProvider tokenProvider, AuditLogService auditLogService) {
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.auditLogService = auditLogService;
    }

    public AuthResponse login(AuthRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String token = tokenProvider.generateToken(authentication);

            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

            auditLogService.logAction(
                    "USER_LOGIN",
                    "/api/auth/login",
                    "User " + userPrincipal.getUsername() + " (" + userPrincipal.getRole() + ") logged in successfully.",
                    userPrincipal.getAssignedBaseId(),
                    200
            );

            return new AuthResponse(
                    token,
                    userPrincipal.getId(),
                    userPrincipal.getUsername(),
                    userPrincipal.getFullName(),
                    userPrincipal.getRole(),
                    userPrincipal.getAssignedBaseId(),
                    null
            );
        } catch (Exception ex) {
            auditLogService.logAction(
                    "LOGIN_FAILED",
                    "/api/auth/login",
                    "Failed login attempt for username: " + (request != null ? request.getUsername() : "unknown") + " - " + ex.getMessage(),
                    null,
                    401
            );
            throw ex;
        }
    }
}
