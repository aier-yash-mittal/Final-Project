package com.stationery.request.controller;

import com.stationery.request.dto.RequestDto;
import com.stationery.request.entity.Request;
import com.stationery.request.service.RequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/requests")
@Tag(name = "Requests", description = "Student Request Management APIs")
public class RequestController {

    @Autowired
    private RequestService requestService;

    @PostMapping
    @Operation(summary = "Create a request", description = "Student creates a request for items")
    public ResponseEntity<Request> createRequest(
            @RequestBody RequestDto requestDto, 
            @RequestHeader("loggedInUser") String studentEmail) { // Extracted from JWT by API Gateway
        return ResponseEntity.ok(requestService.createRequest(requestDto, studentEmail));
    }

    @GetMapping
    @Operation(summary = "Get all requests", description = "Admin view with pagination and filtering")
    public ResponseEntity<org.springframework.data.domain.Page<Request>> getAllRequests(
            @RequestParam(required = false, defaultValue = "ALL") String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy) {
        return ResponseEntity.ok(requestService.getAllRequests(status, org.springframework.data.domain.PageRequest.of(page, size, org.springframework.data.domain.Sort.by(sortBy).descending())));
    }

    @GetMapping("/my-requests")
    @Operation(summary = "Get my requests", description = "Student view with pagination and filtering")
    public ResponseEntity<org.springframework.data.domain.Page<Request>> getMyRequests(
            @RequestHeader("loggedInUser") String studentEmail,
            @RequestParam(required = false, defaultValue = "ALL") String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy) {
        return ResponseEntity.ok(requestService.getRequestsByStudent(studentEmail, status, org.springframework.data.domain.PageRequest.of(page, size, org.springframework.data.domain.Sort.by(sortBy).descending())));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get request by ID")
    public ResponseEntity<Request> getRequestById(@PathVariable Long id) {
        return ResponseEntity.ok(requestService.getRequestById(id));
    }

    @PutMapping("/approve/{id}")
    @Operation(summary = "Approve request", description = "Admin only")
    public ResponseEntity<Request> approveRequest(@PathVariable Long id) {
        return ResponseEntity.ok(requestService.approveRequest(id));
    }

    @PutMapping("/reject/{id}")
    @Operation(summary = "Reject request", description = "Admin only")
    public ResponseEntity<Request> rejectRequest(@PathVariable Long id, @RequestParam String reason) {
        return ResponseEntity.ok(requestService.rejectRequest(id, reason));
    }

    @PutMapping("/fulfil/{id}")
    @Operation(summary = "Fulfil request", description = "Admin only. This triggers Feign call to Inventory.")
    public ResponseEntity<Request> fulfilRequest(@PathVariable Long id) {
        return ResponseEntity.ok(requestService.fulfilRequest(id));
    }
}
