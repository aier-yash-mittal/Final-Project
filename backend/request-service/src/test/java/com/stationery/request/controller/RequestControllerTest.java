package com.stationery.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stationery.request.dto.RequestDto;
import com.stationery.request.entity.Request;
import com.stationery.request.entity.RequestGroup;
import com.stationery.request.service.RequestService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RequestController.class)
@AutoConfigureMockMvc(addFilters = false)
public class RequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RequestService requestService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreateRequest() throws Exception {
        RequestDto dto = new RequestDto();
        dto.setItemId(1L);
        dto.setQuantity(2);

        Request responseReq = new Request();
        responseReq.setId(10L);
        responseReq.setStatus("PENDING");

        Mockito.when(requestService.createRequest(any(RequestDto.class), anyString())).thenReturn(responseReq);

        mockMvc.perform(post("/requests")
                .header("loggedInUser", "student@test.com")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void testCreateOrder() throws Exception {
        RequestDto dto = new RequestDto();
        dto.setItemId(1L);
        dto.setQuantity(2);

        RequestGroup group = new RequestGroup();
        group.setId(5L);
        group.setStudentEmail("student@test.com");

        Mockito.when(requestService.createOrder(any(), anyString())).thenReturn(group);

        mockMvc.perform(post("/requests/order")
                .header("loggedInUser", "student@test.com")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Arrays.asList(dto))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.studentEmail").value("student@test.com"));
    }

    @Test
    void testGetAllRequests() throws Exception {
        Request responseReq = new Request();
        responseReq.setId(10L);
        Page<Request> page = new PageImpl<>(Collections.singletonList(responseReq));

        Mockito.when(requestService.getAllRequests(anyString(), any(PageRequest.class))).thenReturn(page);

        mockMvc.perform(get("/requests")
                .param("status", "ALL")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(10));
    }

    @Test
    void testGetMyRequests() throws Exception {
        Request responseReq = new Request();
        responseReq.setId(10L);
        Page<Request> page = new PageImpl<>(Collections.singletonList(responseReq));

        Mockito.when(requestService.getRequestsByStudent(anyString(), anyString(), any(PageRequest.class))).thenReturn(page);

        mockMvc.perform(get("/requests/my-requests")
                .header("loggedInUser", "student@test.com")
                .param("status", "ALL")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(10));
    }

    @Test
    void testApproveRequest() throws Exception {
        Request responseReq = new Request();
        responseReq.setId(10L);
        responseReq.setStatus("APPROVED");

        Mockito.when(requestService.approveRequest(10L)).thenReturn(responseReq);

        mockMvc.perform(put("/requests/approve/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    void testRejectRequest() throws Exception {
        Request responseReq = new Request();
        responseReq.setId(10L);
        responseReq.setStatus("REJECTED");
        responseReq.setRejectionReason("Out of stock");

        Mockito.when(requestService.rejectRequest(10L, "Out of stock")).thenReturn(responseReq);

        mockMvc.perform(put("/requests/reject/10")
                .param("reason", "Out of stock"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("REJECTED"));
    }

    @Test
    void testFulfilRequest() throws Exception {
        Request responseReq = new Request();
        responseReq.setId(10L);
        responseReq.setStatus("FULFILLED");

        Mockito.when(requestService.fulfilRequest(10L)).thenReturn(responseReq);

        mockMvc.perform(put("/requests/fulfil/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("FULFILLED"));
    }
}
