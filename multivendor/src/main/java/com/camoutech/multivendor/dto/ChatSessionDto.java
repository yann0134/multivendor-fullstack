package com.camoutech.multivendor.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatSessionDto {
    private Long id;
    private String sessionId;
    private Long userId;
    private String userEmail;
    private String title;
    private Boolean isActive;
    private Integer messageCount;
    private Long totalTokens;
    private Double averageResponseTime;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastActivityAt;
    private String duration;
    private List<ChatMessageDto> messages;
    
    public ChatSessionDto(String sessionId, String title, Integer messageCount, LocalDateTime createdAt) {
        this.sessionId = sessionId;
        this.title = title;
        this.messageCount = messageCount;
        this.createdAt = createdAt;
    }
}
