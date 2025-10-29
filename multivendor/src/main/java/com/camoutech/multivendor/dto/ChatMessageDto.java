package com.camoutech.multivendor.dto;

import com.camoutech.multivendor.entity.ChatMessage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageDto {
    private Long id;
    private String content;
    private Boolean isUser;
    private Double responseTime;
    private Long tokens;
    private String rating;
    private Integer messageOrder;
    private LocalDateTime createdAt;
    private String formattedTime;
    private String formattedDate;
    
    public ChatMessageDto(String content, Boolean isUser) {
        this.content = content;
        this.isUser = isUser;
    }
    
    public ChatMessageDto(String content, Boolean isUser, Double responseTime, Long tokens) {
        this.content = content;
        this.isUser = isUser;
        this.responseTime = responseTime;
        this.tokens = tokens;
    }
    
    // Méthodes utilitaires
    public boolean isFromUser() {
        return Boolean.TRUE.equals(isUser);
    }
    
    public boolean isFromAI() {
        return Boolean.FALSE.equals(isUser);
    }
}
