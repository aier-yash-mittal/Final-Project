package com.stationery.inventory.service;

import com.stationery.inventory.entity.StationeryItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ItemService {
    StationeryItem createItem(StationeryItem item);
    StationeryItem updateItem(Long id, StationeryItem item);
    void deleteItem(Long id);
    StationeryItem getItemById(Long id);
    Page<StationeryItem> getAllItems(Pageable pageable);
    List<StationeryItem> getItemsByCategory(String category);
    Page<StationeryItem> searchItems(String name, Pageable pageable);
    void deductQuantity(Long id, Integer quantity);
    List<StationeryItem> getLowStockItems();
}
