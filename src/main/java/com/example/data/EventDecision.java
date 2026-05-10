package com.example.data;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class EventDecision {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "event_id")
    private String eventId;
    private String message;
    private String severity;
    private String reason;
    private LocalDateTime timestamp;
    
    @Column(columnDefinition = "vector(1536)")
    private float[] embedding;  

    @Column(name = "created_at", updatable = false, insertable = false)
    private LocalDateTime createdAt;
}
