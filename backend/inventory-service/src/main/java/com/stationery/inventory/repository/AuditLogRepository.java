package com.stationery.inventory.repository;

import com.stationery.inventory.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
}

//no methods as JpaRepository provides CRUD operations for AuditLog entity and that are sufficient for our use case.