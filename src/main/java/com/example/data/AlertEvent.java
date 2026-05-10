package com.example.data;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class AlertEvent {
    private String eventId;
    private String message;
    private LocalDateTime timestamp;
}
