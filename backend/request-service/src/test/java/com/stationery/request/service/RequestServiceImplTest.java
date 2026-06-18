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
        verify(inventoryClient, never()).deductQuantity(anyLong(), anyInt());
    }
}
