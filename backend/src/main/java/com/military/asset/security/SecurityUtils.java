package com.military.asset.security;

import com.military.asset.entity.Role;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {

    public static UserPrincipal getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal userPrincipal) {
            return userPrincipal;
        }
        return null;
    }

    /**
     * Validates base access for the authenticated user.
     * If the user is a BASE_COMMANDER, enforces that operations/queries match their assigned base ID.
     * Throws AccessDeniedException (HTTP 403) if they attempt to access another base.
     */
    public static Long validateAndGetBaseScope(Long requestedBaseId) {
        UserPrincipal currentUser = getCurrentUser();
        if (currentUser == null) {
            return requestedBaseId;
        }

        if (currentUser.getRole() == Role.BASE_COMMANDER) {
            Long assignedBaseId = currentUser.getAssignedBaseId();
            if (assignedBaseId == null) {
                throw new AccessDeniedException("Access Denied: Base Commander has no assigned base.");
            }
            if (requestedBaseId != null && !requestedBaseId.equals(assignedBaseId)) {
                throw new AccessDeniedException("Access Denied: Base Commander cannot access data for Base ID " + requestedBaseId);
            }
            return assignedBaseId;
        }

        return requestedBaseId;
    }

    /**
     * Validates that a write action on a source base is permitted for the current user.
     */
    public static void checkBaseWritePermission(Long targetBaseId) {
        UserPrincipal currentUser = getCurrentUser();
        if (currentUser == null) {
            return;
        }
        if (currentUser.getRole() == Role.BASE_COMMANDER) {
            Long assignedBaseId = currentUser.getAssignedBaseId();
            if (assignedBaseId == null || !assignedBaseId.equals(targetBaseId)) {
                throw new AccessDeniedException("Access Denied: You can only perform operations for your assigned Base ID (" + assignedBaseId + ")");
            }
        }
    }
}
