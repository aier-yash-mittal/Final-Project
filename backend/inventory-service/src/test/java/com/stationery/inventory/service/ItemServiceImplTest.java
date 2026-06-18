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

public class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;

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
}
