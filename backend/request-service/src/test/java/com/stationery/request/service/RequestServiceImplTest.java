package com.stationery.request.service;

import com.stationery.request.client.InventoryClient;
import com.stationery.request.entity.Request;
import com.stationery.request.repository.RequestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class RequestServiceImplTest {

    @Mock
    private RequestRepository requestRepository;

    @Mock
    private InventoryClient inventoryClient;

    @InjectMocks
    private RequestServiceImpl requestService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFulfilRequestSuccess() {
        Request req = new Request();
        req.setId(1L);
        req.setStatus("APPROVED");
        req.setItemId(10L);
        req.setQuantity(5);

        when(requestRepository.findById(1L)).thenReturn(Optional.of(req));
        when(requestRepository.save(any(Request.class))).thenReturn(req);

        Request fulfilledReq = requestService.fulfilRequest(1L);

        assertEquals("FULFILLED", fulfilledReq.getStatus());
        verify(inventoryClient, times(1)).deductQuantity(10L, 5);
    }

    @Test
    void testFulfilRequestFailureNotApproved() {
        Request req = new Request();
        req.setId(1L);
        req.setStatus("PENDING");

        when(requestRepository.findById(1L)).thenReturn(Optional.of(req));

        Exception exception = assertThrows(RuntimeException.class, () -> requestService.fulfilRequest(1L));
        assertEquals("Only approved requests can be fulfilled", exception.getMessage());
        assertEquals("Only approved requests can be fulfilled", exception.getMessage());
        verify(inventoryClient, never()).deductQuantity(anyLong(), anyInt());
    }

    @Mock
    private com.stationery.request.repository.RequestGroupRepository requestGroupRepository;

    @Test
    void testCreateRequest() {
        com.stationery.request.dto.RequestDto dto = new com.stationery.request.dto.RequestDto();
        dto.setItemId(1L);
        dto.setQuantity(2);

        Request savedRequest = new Request();
        savedRequest.setId(10L);
        savedRequest.setItemId(1L);
        savedRequest.setQuantity(2);
        savedRequest.setStatus("PENDING");
        savedRequest.setStudentEmail("test@test.com");

        when(requestRepository.save(any(Request.class))).thenReturn(savedRequest);

        Request result = requestService.createRequest(dto, "test@test.com");

        assertEquals(10L, result.getId());
        assertEquals("PENDING", result.getStatus());
        verify(requestRepository, times(1)).save(any(Request.class));
    }

    @Test
    void testCreateOrder() {
        com.stationery.request.dto.RequestDto dto = new com.stationery.request.dto.RequestDto();
        dto.setItemId(1L);
        dto.setQuantity(2);

        com.stationery.request.entity.RequestGroup group = new com.stationery.request.entity.RequestGroup();
        group.setId(5L);
        group.setStudentEmail("test@test.com");

        when(requestGroupRepository.save(any(com.stationery.request.entity.RequestGroup.class))).thenReturn(group);
        when(requestRepository.save(any(Request.class))).thenReturn(new Request());

        com.stationery.request.entity.RequestGroup result = requestService.createOrder(java.util.Collections.singletonList(dto), "test@test.com");

        assertEquals(5L, result.getId());
        assertEquals("test@test.com", result.getStudentEmail());
        verify(requestGroupRepository, times(1)).save(any());
        verify(requestRepository, times(1)).save(any(Request.class));
    }

    @Test
    void testGetAllRequestsWithStatus() {
        org.springframework.data.domain.Page<Request> page = new org.springframework.data.domain.PageImpl<>(java.util.Collections.emptyList());
        when(requestRepository.findByStatus(eq("PENDING"), any(org.springframework.data.domain.Pageable.class))).thenReturn(page);
        
        org.springframework.data.domain.Page<Request> result = requestService.getAllRequests("PENDING", org.springframework.data.domain.PageRequest.of(0, 10));
        
        assertNotNull(result);
        verify(requestRepository, times(1)).findByStatus(eq("PENDING"), any());
    }

    @Test
    void testGetAllRequestsWithoutStatus() {
        org.springframework.data.domain.Page<Request> page = new org.springframework.data.domain.PageImpl<>(java.util.Collections.emptyList());
        when(requestRepository.findAll(any(org.springframework.data.domain.Pageable.class))).thenReturn(page);
        
        org.springframework.data.domain.Page<Request> result = requestService.getAllRequests("ALL", org.springframework.data.domain.PageRequest.of(0, 10));
        
        assertNotNull(result);
        verify(requestRepository, times(1)).findAll(any(org.springframework.data.domain.Pageable.class));
    }

    @Test
    void testGetRequestByIdNotFound() {
        when(requestRepository.findById(1L)).thenReturn(Optional.empty());
        Exception exception = assertThrows(RuntimeException.class, () -> requestService.getRequestById(1L));
        assertEquals("Request not found", exception.getMessage());
    }

    @Test
    void testGetRequestsByStudentWithStatus() {
        org.springframework.data.domain.Page<Request> page = new org.springframework.data.domain.PageImpl<>(java.util.Collections.emptyList());
        when(requestRepository.findByStudentEmailAndStatus(eq("test@test.com"), eq("APPROVED"), any())).thenReturn(page);
        
        org.springframework.data.domain.Page<Request> result = requestService.getRequestsByStudent("test@test.com", "APPROVED", org.springframework.data.domain.PageRequest.of(0, 10));
        
        assertNotNull(result);
        verify(requestRepository, times(1)).findByStudentEmailAndStatus(anyString(), anyString(), any());
    }

    @Test
    void testGetRequestsByStudentWithoutStatus() {
        org.springframework.data.domain.Page<Request> page = new org.springframework.data.domain.PageImpl<>(java.util.Collections.emptyList());
        when(requestRepository.findByStudentEmail(eq("test@test.com"), any())).thenReturn(page);
        
        org.springframework.data.domain.Page<Request> result = requestService.getRequestsByStudent("test@test.com", "ALL", org.springframework.data.domain.PageRequest.of(0, 10));
        
        assertNotNull(result);
        verify(requestRepository, times(1)).findByStudentEmail(anyString(), any());
    }

    @Test
    void testApproveRequest() {
        Request req = new Request();
        req.setId(1L);
        req.setStatus("PENDING");

        when(requestRepository.findById(1L)).thenReturn(Optional.of(req));
        when(requestRepository.save(any(Request.class))).thenReturn(req);

        Request result = requestService.approveRequest(1L);

        assertEquals("APPROVED", result.getStatus());
        verify(requestRepository, times(1)).save(req);
    }

    @Test
    void testRejectRequest() {
        Request req = new Request();
        req.setId(1L);
        req.setStatus("PENDING");

        when(requestRepository.findById(1L)).thenReturn(Optional.of(req));
        when(requestRepository.save(any(Request.class))).thenReturn(req);

        Request result = requestService.rejectRequest(1L, "Out of stock");

        assertEquals("REJECTED", result.getStatus());
        assertEquals("Out of stock", result.getRejectionReason());
        verify(requestRepository, times(1)).save(req);
    }
}
