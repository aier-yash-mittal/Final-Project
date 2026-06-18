package com.stationery.request.service;

import com.stationery.request.dto.RequestDto;
import com.stationery.request.entity.Request;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface RequestService {
    Request createRequest(RequestDto requestDto, String studentEmail);
    Page<Request> getAllRequests(String status, Pageable pageable);
    Request getRequestById(Long id);
    Page<Request> getRequestsByStudent(String studentEmail, String status, Pageable pageable);
    Request approveRequest(Long id);
    Request rejectRequest(Long id, String reason);
    Request fulfilRequest(Long id);
}
