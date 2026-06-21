package com.stationery.inventory.service;

import com.stationery.inventory.entity.StationeryItem;
import com.stationery.inventory.repository.ItemRepository;
import com.stationery.inventory.entity.AuditLog;
import com.stationery.inventory.repository.AuditLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private AuditLogRepository auditLogRepository;

    private void logAudit(Long itemId, Integer oldQty, Integer newQty, String action) {
        AuditLog log = new AuditLog();
        log.setItemId(itemId);
        log.setOldQuantity(oldQty);
        log.setNewQuantity(newQty);
        log.setAction(action);
        auditLogRepository.save(log);
    }

    @Override
    public StationeryItem createItem(StationeryItem item) {
        StationeryItem saved = itemRepository.save(item);
        logAudit(saved.getId(), 0, saved.getAvailableQuantity(), "CREATED");
        return saved;
    }

    @Override
    public StationeryItem updateItem(Long id, StationeryItem updatedItem) {
        StationeryItem existing = getItemById(id);
        Integer oldQty = existing.getAvailableQuantity();
        
        existing.setName(updatedItem.getName());
        existing.setCategory(updatedItem.getCategory());
        existing.setUnit(updatedItem.getUnit());
        existing.setAvailableQuantity(updatedItem.getAvailableQuantity());
        existing.setMinimumQuantity(updatedItem.getMinimumQuantity());
        
        StationeryItem saved = itemRepository.save(existing);
        logAudit(saved.getId(), oldQty, saved.getAvailableQuantity(), "UPDATED");
        return saved;
    }

    @Override
    public void deleteItem(Long id) {
        StationeryItem existing = getItemById(id);
        logAudit(existing.getId(), existing.getAvailableQuantity(), 0, "DELETED");  //in delete audit log is done before beacuse after deletion we cannot get the item details
        itemRepository.deleteById(id);
    }

    @Override
    public StationeryItem getItemById(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found with id: " + id));
    }

    @Override
    public Page<StationeryItem> getAllItems(Pageable pageable) {
        return itemRepository.findAll(pageable);
    }

    @Override
    public List<StationeryItem> getItemsByCategory(String category) {
        return itemRepository.findByCategory(category);
    }

    @Override
    public Page<StationeryItem> searchItems(String name, Pageable pageable) {
        return itemRepository.findByNameContainingIgnoreCase(name, pageable);
    }

    @Override
    public void deductQuantity(Long id, Integer quantity) {
        StationeryItem item = getItemById(id);
        Integer oldQty = item.getAvailableQuantity();
        if (oldQty < quantity) {
            throw new RuntimeException("Not enough quantity available");
        }
        item.setAvailableQuantity(oldQty - quantity);
        itemRepository.save(item);
        logAudit(item.getId(), oldQty, item.getAvailableQuantity(), "DEDUCTED");
    }

    @Override
    public List<StationeryItem> getLowStockItems() {
        return itemRepository.findLowStockItems();
    }
}

