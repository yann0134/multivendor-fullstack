package com.camoutech.multivendor.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "chat_sessions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatSession {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "session_id", unique = true, nullable = false)
    private String sessionId;
    
    @Column(name = "user_id")
    private Long userId;
    
    @Column(name = "user_email")
    private String userEmail;
    
    @Column(name = "title", length = 255)
    private String title;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @Column(name = "message_count")
    private Integer messageCount = 0;
    
    @Column(name = "total_tokens")
    private Long totalTokens = 0L;
    
    @Column(name = "average_response_time")
    private Double averageResponseTime = 0.0;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "last_activity_at")
    private LocalDateTime lastActivityAt;
    
    // Relation avec les messages
    @OneToMany(mappedBy = "chatSession", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ChatMessage> messages = new ArrayList<>();
    
    // Méthodes utilitaires
    public void addMessage(ChatMessage message) {
        messages.add(message);
        message.setChatSession(this);
        messageCount++;
        lastActivityAt = LocalDateTime.now();
    }
    
    public void updateMetrics() {
        if (!messages.isEmpty()) {
            // Calculer le temps de réponse moyen
            List<ChatMessage> aiMessages = messages.stream()
                .filter(msg -> !msg.getIsUser() && msg.getResponseTime() != null)
                .toList();
            
            if (!aiMessages.isEmpty()) {
                averageResponseTime = aiMessages.stream()
                    .mapToDouble(ChatMessage::getResponseTime)
                    .average()
                    .orElse(0.0);
            }
            
            // Calculer le total des tokens
            totalTokens = messages.stream()
                .filter(msg -> !msg.getIsUser() && msg.getTokens() != null)
                .mapToLong(ChatMessage::getTokens)
                .sum();
        }
    }
    
    public String getFormattedDuration() {
        if (lastActivityAt != null && createdAt != null) {
            long minutes = java.time.Duration.between(createdAt, lastActivityAt).toMinutes();
            long hours = minutes / 60;
            minutes = minutes % 60;
            
            if (hours > 0) {
                return String.format("%dh %dm", hours, minutes);
            } else {
                return String.format("%dm", minutes);
            }
        }
        return "0m";
    }
}
