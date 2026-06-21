package com.stationery.inventory.repository;

import com.stationery.inventory.entity.StationeryItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<StationeryItem, Long> {
    List<StationeryItem> findByCategory(String category); 
    Page<StationeryItem> findByNameContainingIgnoreCase(String name, Pageable pageable); //pageable is used for pagination and sorting

    @Query("SELECT i FROM StationeryItem i WHERE i.availableQuantity <= i.minimumQuantity")
    List<StationeryItem> findLowStockItems();  //custom query  used for finding low stock items based on available quantity and minimum quantity
}
