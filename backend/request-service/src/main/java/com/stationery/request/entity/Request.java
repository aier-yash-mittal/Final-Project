package com.stationery.request.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "requests")
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_email") // Changed from student_id to simplify using JWT claims
    private String studentEmail;
    
    @Column(name = "item_id")
    private Long itemId;
    
    private Integer quantity;
    
    private String status; // PENDING, APPROVED, REJECTED, FULFILLED
    
    @Column(name = "rejection_reason")
    private String rejectionReason;

    @Column(name = "request_group_id")
    private Long requestGroupId;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if(status == null) {
            status = "PENDING";
        }
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    @Transient
    public String getRequestId() {
        return this.id != null ? "REQ-" + (1000 + this.id) : null;
    }
    
    public String getStudentEmail() { return studentEmail; }
    public void setStudentEmail(String studentEmail) { this.studentEmail = studentEmail; }
    public Long getItemId() { return itemId; }
    public void setItemId(Long itemId) { this.itemId = itemId; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }
    public Long getRequestGroupId() { return requestGroupId; }
    public void setRequestGroupId(Long requestGroupId) { this.requestGroupId = requestGroupId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
