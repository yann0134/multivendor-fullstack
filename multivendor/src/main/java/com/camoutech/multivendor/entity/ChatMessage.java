package com.camoutech.multivendor.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_messages")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_session_id", nullable = false)
    private ChatSession chatSession;
    
    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    private String content;
    
    @Column(name = "is_user", nullable = false)
    private Boolean isUser;
    
    @Column(name = "response_time")
    private Double responseTime;
    
    @Column(name = "tokens")
    private Long tokens;
    
    @Column(name = "rating")
    @Enumerated(EnumType.STRING)
    private MessageRating rating;
    
    @Column(name = "message_order")
    private Integer messageOrder;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    // Enum pour les évaluations
    public enum MessageRating {
        POSITIVE, NEGATIVE, NEUTRAL
    }
    
    // Constructeurs utilitaires
    public ChatMessage(String content, Boolean isUser) {
        this.content = content;
        this.isUser = isUser;
    }
    
    public ChatMessage(String content, Boolean isUser, Double responseTime, Long tokens) {
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
    
    public String getFormattedTime() {
        return createdAt.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"));
    }
    
    public String getFormattedDate() {
        return createdAt.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }
    
    public String getTruncatedContent(int maxLength) {
        if (content.length() <= maxLength) {
            return content;
        }
        return content.substring(0, maxLength) + "...";
    }
}
