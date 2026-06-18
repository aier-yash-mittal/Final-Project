package com.stationery.request.repository;

import com.stationery.request.entity.Request;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {
    Page<Request> findByStudentEmail(String studentEmail, Pageable pageable);
    Page<Request> findByStatus(String status, Pageable pageable);
    Page<Request> findByStudentEmailAndStatus(String studentEmail, String status, Pageable pageable);
    List<Request> findByStudentEmail(String studentEmail);
    List<Request> findByStatus(String status);
}
