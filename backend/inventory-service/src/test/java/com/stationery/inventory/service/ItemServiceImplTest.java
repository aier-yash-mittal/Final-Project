package com.stationery.inventory.service;

import com.stationery.inventory.entity.StationeryItem;
import com.stationery.inventory.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import com.stationery.inventory.repository.AuditLogRepository;

public class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private AuditLogRepository auditLogRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testDeductQuantitySuccess() {
        StationeryItem item = new StationeryItem();
        item.setId(1L);
        item.setAvailableQuantity(100);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        itemService.deductQuantity(1L, 10);

        assertEquals(90, item.getAvailableQuantity());
        verify(itemRepository, times(1)).save(item);
    }

    @Test
    void testDeductQuantityFailureNotEnoughStock() {
        StationeryItem item = new StationeryItem();
        item.setId(1L);
        item.setAvailableQuantity(5);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        Exception exception = assertThrows(RuntimeException.class, () -> itemService.deductQuantity(1L, 10));
        assertEquals("Not enough quantity available", exception.getMessage());
    }

    @Test
    void testGetItemByIdNotFound() {
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());
        Exception exception = assertThrows(RuntimeException.class, () -> itemService.getItemById(1L));
        assertEquals("Item not found with id: 1", exception.getMessage());
    }

    @Test
    void testUpdateItemSuccess() {
        StationeryItem existingItem = new StationeryItem();
        existingItem.setId(1L);
        existingItem.setName("Old Pen");

        StationeryItem newItem = new StationeryItem();
        newItem.setName("New Pen");

        when(itemRepository.findById(1L)).thenReturn(Optional.of(existingItem));
        when(itemRepository.save(any(StationeryItem.class))).thenReturn(existingItem);

        StationeryItem result = itemService.updateItem(1L, newItem);
        assertEquals("New Pen", result.getName());
        verify(itemRepository, times(1)).save(existingItem);
    }

    @Test
    void testDeleteItemSuccess() {
        StationeryItem existingItem = new StationeryItem();
        when(itemRepository.findById(1L)).thenReturn(Optional.of(existingItem));
        doNothing().when(itemRepository).deleteById(1L);
        itemService.deleteItem(1L);
        verify(itemRepository, times(1)).deleteById(1L);
    }

}
