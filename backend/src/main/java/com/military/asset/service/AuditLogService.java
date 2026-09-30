package com.military.asset.service;

import com.military.asset.entity.AuditLog;
import com.military.asset.entity.Base;
import com.military.asset.repository.AuditLogRepository;
import com.military.asset.repository.BaseRepository;
import com.military.asset.security.UserPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final BaseRepository baseRepository;

    public AuditLogService(AuditLogRepository auditLogRepository, BaseRepository baseRepository) {
        this.auditLogRepository = auditLogRepository;
        this.baseRepository = baseRepository;
    }

    @Transactional
    public void logAction(String action, String resource, String details, Long baseId, int statusCode) {
        AuditLog log = new AuditLog();
        log.setTimestamp(LocalDateTime.now());
        log.setAction(action);
        log.setResource(resource);
        log.setDetails(details);
        log.setStatusCode(statusCode);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof UserPrincipal principal) {
            log.setUsername(principal.getUsername());
            log.setUserRole(principal.getRole().name());
            if (baseId != null) {
                baseRepository.findById(baseId).ifPresent(log::setBase);
            } else if (principal.getAssignedBaseId() != null) {
                baseRepository.findById(principal.getAssignedBaseId()).ifPresent(log::setBase);
            }
        } else {
            log.setUsername("SYSTEM / ANONYMOUS");
            log.setUserRole("NONE");
            if (baseId != null) {
                baseRepository.findById(baseId).ifPresent(log::setBase);
            }
        }

        auditLogRepository.save(log);
    }

    public List<AuditLog> getRecentAuditLogs() {
        return auditLogRepository.findTop100ByOrderByTimestampDesc();
    }
}
