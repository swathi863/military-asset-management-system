package com.military.asset.dto;

import com.military.asset.entity.Role;

public class AuthResponse {
    private String token;
    private String tokenType = "Bearer";
    private Long id;
    private String username;
    private String fullName;
    private Role role;
    private Long assignedBaseId;
    private String assignedBaseName;

    public AuthResponse() {}

    public AuthResponse(String token, Long id, String username, String fullName, Role role, Long assignedBaseId, String assignedBaseName) {
        this.token = token;
        this.id = id;
        this.username = username;
        this.fullName = fullName;
        this.role = role;
        this.assignedBaseId = assignedBaseId;
        this.assignedBaseName = assignedBaseName;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getTokenType() { return tokenType; }
    public void setTokenType(String tokenType) { this.tokenType = tokenType; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public Long getAssignedBaseId() { return assignedBaseId; }
    public void setAssignedBaseId(Long assignedBaseId) { this.assignedBaseId = assignedBaseId; }

    public String getAssignedBaseName() { return assignedBaseName; }
    public void setAssignedBaseName(String assignedBaseName) { this.assignedBaseName = assignedBaseName; }
}
