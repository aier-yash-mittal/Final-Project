package com.stationery.request.service;

import com.stationery.request.client.InventoryClient;
import com.stationery.request.dto.RequestDto;
import com.stationery.request.entity.Request;
import com.stationery.request.entity.RequestGroup;
import com.stationery.request.repository.RequestGroupRepository;
import com.stationery.request.repository.RequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RequestServiceImpl implements RequestService {

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private InventoryClient inventoryClient;

    @Autowired
    private RequestGroupRepository requestGroupRepository;

    @Override
    public Request createRequest(RequestDto requestDto, String studentEmail) {
        Request request = new Request();
        request.setItemId(requestDto.getItemId());
        request.setQuantity(requestDto.getQuantity());
        request.setStudentEmail(studentEmail);
        request.setStatus("PENDING");
        return requestRepository.save(request);
    }

    @Override
    public RequestGroup createOrder(List<RequestDto> requests, String studentEmail) {
        RequestGroup group = new RequestGroup();
        group.setStudentEmail(studentEmail);
        group = requestGroupRepository.save(group);

        for (RequestDto dto : requests) {
            Request request = new Request();
            request.setItemId(dto.getItemId());
            request.setQuantity(dto.getQuantity());
            request.setStudentEmail(studentEmail);
            request.setStatus("PENDING");
            request.setRequestGroupId(group.getId());  // Associate request with the group
            requestRepository.save(request);
        }
        return group;
    }

    @Override
    public Page<Request> getAllRequests(String status, Pageable pageable) {
        if (status != null && !status.isEmpty() && !status.equalsIgnoreCase("ALL")) {
            return requestRepository.findByStatus(status.toUpperCase(), pageable);
        }
        return requestRepository.findAll(pageable);
    }

    @Override
    public Request getRequestById(Long id) {
        return requestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Request not found"));
    }

    @Override
    public Page<Request> getRequestsByStudent(String studentEmail, String status, Pageable pageable) {
        if (status != null && !status.isEmpty() && !status.equalsIgnoreCase("ALL")) {  
            return requestRepository.findByStudentEmailAndStatus(studentEmail, status.toUpperCase(), pageable);
        }
        return requestRepository.findByStudentEmail(studentEmail, pageable);
    }

    @Override
    public Request approveRequest(Long id) {
        Request request = getRequestById(id);
        request.setStatus("APPROVED");
        return requestRepository.save(request);
    }

    @Override
    public Request rejectRequest(Long id, String reason) {
        Request request = getRequestById(id);
        request.setStatus("REJECTED");
        request.setRejectionReason(reason);
        return requestRepository.save(request);
    }

    @Override
    public Request fulfilRequest(Long id) {
        Request request = getRequestById(id);
        if (!"APPROVED".equals(request.getStatus())) {
            throw new RuntimeException("Only approved requests can be fulfilled");
        }
        
        // Call Inventory Service via Feign to deduct stock
        inventoryClient.deductQuantity(request.getItemId(), request.getQuantity());
        
        request.setStatus("FULFILLED");
        return requestRepository.save(request);
    }
}
