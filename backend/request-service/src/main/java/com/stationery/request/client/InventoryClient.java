package com.stationery.request.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

// Talks to the Inventory Service registered in Eureka as "inventory-service"
@FeignClient(name = "inventory-service")
public interface InventoryClient {

    @PutMapping("/items/{id}/deduct")
    String deductQuantity(@PathVariable("id") Long id, @RequestParam("quantity") Integer quantity);
}
