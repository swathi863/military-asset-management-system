package com.military.asset.repository;

import com.military.asset.entity.Assignment;
import com.military.asset.entity.AssignmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {

    @Query("SELECT a FROM Assignment a WHERE " +
           "(:baseId IS NULL OR a.base.id = :baseId) AND " +
           "(:equipmentTypeId IS NULL OR a.equipmentType.id = :equipmentTypeId) AND " +
           "(:startDate IS NULL OR a.assignmentDate >= :startDate) AND " +
           "(:endDate IS NULL OR a.assignmentDate <= :endDate) " +
           "ORDER BY a.assignmentDate DESC")
    List<Assignment> filterAssignments(
            @Param("baseId") Long baseId,
            @Param("equipmentTypeId") Long equipmentTypeId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query("SELECT COALESCE(SUM(a.quantity), 0) FROM Assignment a WHERE " +
           "(:baseId IS NULL OR a.base.id = :baseId) AND " +
           "(:equipmentTypeId IS NULL OR a.equipmentType.id = :equipmentTypeId) AND " +
           "(:startDate IS NULL OR a.assignmentDate >= :startDate) AND " +
           "(:endDate IS NULL OR a.assignmentDate <= :endDate) AND " +
           "(:status IS NULL OR a.status = :status)")
    Integer sumAssignedQuantity(
            @Param("baseId") Long baseId,
            @Param("equipmentTypeId") Long equipmentTypeId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("status") AssignmentStatus status
    );
}
