package com.stationery.inventory.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stationery.inventory.entity.StationeryItem;
import com.stationery.inventory.service.ItemService;
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

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService itemService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreateItem() throws Exception {
        StationeryItem item = new StationeryItem();
        item.setName("Pen");
        item.setCategory("Stationery");
        item.setUnit("Box");
        item.setAvailableQuantity(10);
        item.setMinimumQuantity(5);

        StationeryItem responseItem = new StationeryItem();
        responseItem.setId(1L);
        responseItem.setName("Pen");

        Mockito.when(itemService.createItem(any(StationeryItem.class))).thenReturn(responseItem);

        mockMvc.perform(post("/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(item)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Pen"));
    }

    @Test
    void testGetAllItems() throws Exception {
        StationeryItem responseItem = new StationeryItem();
        responseItem.setId(1L);
        responseItem.setName("Pen");
        Page<StationeryItem> page = new PageImpl<>(Collections.singletonList(responseItem));

        Mockito.when(itemService.getAllItems(any(PageRequest.class))).thenReturn(page);

        mockMvc.perform(get("/items")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Pen"));
    }

    @Test
    void testGetItemById() throws Exception {
        StationeryItem responseItem = new StationeryItem();
        responseItem.setId(1L);
        responseItem.setName("Pen");

        Mockito.when(itemService.getItemById(1L)).thenReturn(responseItem);

        mockMvc.perform(get("/items/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Pen"));
    }

    @Test
    void testUpdateItem() throws Exception {
        StationeryItem item = new StationeryItem();
        item.setName("Updated Pen");
        item.setCategory("Stationery");
        item.setUnit("Box");
        item.setAvailableQuantity(20);
        item.setMinimumQuantity(5);

        StationeryItem responseItem = new StationeryItem();
        responseItem.setId(1L);
        responseItem.setName("Updated Pen");

        Mockito.when(itemService.updateItem(anyLong(), any(StationeryItem.class))).thenReturn(responseItem);

        mockMvc.perform(put("/items/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(item)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Pen"));
    }

    @Test
    void testDeleteItem() throws Exception {
        Mockito.doNothing().when(itemService).deleteItem(1L);

        mockMvc.perform(delete("/items/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Item deleted successfully"));
    }

    @Test
    void testDeductQuantity() throws Exception {
        Mockito.doNothing().when(itemService).deductQuantity(1L, 5);

        mockMvc.perform(put("/items/1/deduct")
                .param("quantity", "5"))
                .andExpect(status().isOk())
                .andExpect(content().string("Quantity deducted successfully"));
    }
}
