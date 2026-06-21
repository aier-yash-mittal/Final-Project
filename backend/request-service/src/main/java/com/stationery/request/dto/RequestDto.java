package com.stationery.request.dto;

public class RequestDto {
    private Long itemId;
    private Integer quantity;

    // Getters and Setters
    public Long getItemId() { return itemId; }
    public void setItemId(Long itemId) { this.itemId = itemId; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
}

//dto is used because client only needs itemId and quantity to make a request, and we don't want to expose the entire Request entity to the client. It also allows us to validate the input data before processing the request.