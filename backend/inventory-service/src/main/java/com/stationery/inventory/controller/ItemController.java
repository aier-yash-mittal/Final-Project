package com.stationery.inventory.controller;

import com.stationery.inventory.entity.StationeryItem;
import com.stationery.inventory.service.ItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/items") 
@Tag(name = "Inventory", description = "Stationery Inventory Management APIs")
public class ItemController {

    @Autowired
    private ItemService itemService;

    @PostMapping
    @Operation(summary = "Add new item", description = "Admin only")
    public ResponseEntity<StationeryItem> createItem(@RequestBody StationeryItem item) {
        return ResponseEntity.ok(itemService.createItem(item));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update item details", description = "Admin only")
    public ResponseEntity<StationeryItem> updateItem(@PathVariable Long id, @RequestBody StationeryItem item) {
        return ResponseEntity.ok(itemService.updateItem(id, item));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete item", description = "Admin only")
    public ResponseEntity<String> deleteItem(@PathVariable Long id) {
        itemService.deleteItem(id);
        return ResponseEntity.ok("Item deleted successfully");
    }

    @GetMapping
    @Operation(summary = "Get all items", description = "Supports pagination and sorting")
    public ResponseEntity<Page<StationeryItem>> getAllItems(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy) {
        return ResponseEntity.ok(itemService.getAllItems(PageRequest.of(page, size, Sort.by(sortBy))));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get item by ID")
    public ResponseEntity<StationeryItem> getItemById(@PathVariable Long id) {
        return ResponseEntity.ok(itemService.getItemById(id));
    }

    @GetMapping("/category/{category}")
    @Operation(summary = "Get items by category")
    public ResponseEntity<List<StationeryItem>> getItemsByCategory(@PathVariable String category) {
        return ResponseEntity.ok(itemService.getItemsByCategory(category));
    }

    @GetMapping("/low-stock")
    @Operation(summary = "Get low stock items", description = "Admin only")
    public ResponseEntity<List<StationeryItem>> getLowStockItems() {
        return ResponseEntity.ok(itemService.getLowStockItems());
    }

    @GetMapping("/search")
    @Operation(summary = "Search items by name")
    public ResponseEntity<Page<StationeryItem>> searchItems(
            @RequestParam String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(itemService.searchItems(name, PageRequest.of(page, size)));
    }

    @PutMapping("/{id}/deduct")
    @Operation(summary = "Deduct quantity", description = "Used by Request Service via Feign") //Feign Client os  declarative REST client which connects  two microservices.
    public ResponseEntity<String> deductQuantity(@PathVariable Long id, @RequestParam Integer quantity) {
        itemService.deductQuantity(id, quantity);
        return ResponseEntity.ok("Quantity deducted successfully");
    }
}
