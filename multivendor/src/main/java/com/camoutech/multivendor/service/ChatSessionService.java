package com.camoutech.multivendor.service;

import com.camoutech.multivendor.entity.ChatMessage;
import com.camoutech.multivendor.entity.ChatSession;
import com.camoutech.multivendor.repository.ChatMessageRepository;
import com.camoutech.multivendor.repository.ChatSessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ChatSessionService {
    
    private final ChatSessionRepository chatSessionRepository;
    private final ChatMessageRepository chatMessageRepository;
    
    /**
     * Créer une nouvelle session de chat
     */
    public ChatSession createSession(Long userId, String userEmail) {
        String sessionId = generateSessionId();
        
        ChatSession session = new ChatSession();
        session.setSessionId(sessionId);
        session.setUserId(userId);
        session.setUserEmail(userEmail);
        session.setTitle("Nouvelle conversation");
        session.setIsActive(true);
        session.setMessageCount(0);
        session.setTotalTokens(0L);
        session.setAverageResponseTime(0.0);
        session.setLastActivityAt(LocalDateTime.now());
        
        ChatSession savedSession = chatSessionRepository.save(session);
        log.info("✅ Nouvelle session créée: {} pour utilisateur: {}", sessionId, userEmail);
        
        return savedSession;
    }
    
    /**
     * Trouver une session par sessionId
     */
    @Transactional(readOnly = true)
    public Optional<ChatSession> findBySessionId(String sessionId) {
        return chatSessionRepository.findBySessionId(sessionId);
    }
    
    /**
     * Trouver les sessions actives d'un utilisateur
     */
    @Transactional(readOnly = true)
    public List<ChatSession> findActiveSessionsByUserId(Long userId) {
        return chatSessionRepository.findByUserIdAndIsActiveTrueOrderByLastActivityAtDesc(userId);
    }
    
    /**
     * Trouver les sessions actives par email
     */
    @Transactional(readOnly = true)
    public List<ChatSession> findActiveSessionsByEmail(String userEmail) {
        return chatSessionRepository.findByUserEmailAndIsActiveTrueOrderByLastActivityAtDesc(userEmail);
    }
    
    /**
     * Trouver les sessions avec pagination
     */
    @Transactional(readOnly = true)
    public Page<ChatSession> findActiveSessionsByUserId(Long userId, Pageable pageable) {
        return chatSessionRepository.findByUserIdAndIsActiveTrueOrderByLastActivityAtDesc(userId, pageable);
    }
    
    /**
     * Trouver les sessions par email avec pagination
     */
    @Transactional(readOnly = true)
    public Page<ChatSession> findActiveSessionsByEmail(String userEmail, Pageable pageable) {
        return chatSessionRepository.findByUserEmailAndIsActiveTrueOrderByLastActivityAtDesc(userEmail, pageable);
    }
    
    /**
     * Ajouter un message à une session
     */
    public ChatMessage addMessage(String sessionId, String content, Boolean isUser, 
                                Double responseTime, Long tokens) {
        Optional<ChatSession> sessionOpt = findBySessionId(sessionId);
        if (sessionOpt.isEmpty()) {
            throw new RuntimeException("Session non trouvée: " + sessionId);
        }
        
        ChatSession session = sessionOpt.get();
        
        // Créer le message
        ChatMessage message = new ChatMessage(content, isUser, responseTime, tokens);
        message.setMessageOrder(session.getMessageCount() + 1);
        
        // Ajouter le message à la session
        session.addMessage(message);
        
        // Sauvegarder
        ChatMessage savedMessage = chatMessageRepository.save(message);
        chatSessionRepository.save(session);
        
        log.info("💬 Message ajouté à la session {}: {} (utilisateur: {})", 
                sessionId, isUser ? "utilisateur" : "IA", isUser);
        
        return savedMessage;
    }
    
    /**
     * Récupérer tous les messages d'une session
     */
    @Transactional(readOnly = true)
    public List<ChatMessage> getSessionMessages(String sessionId) {
        Optional<ChatSession> sessionOpt = findBySessionId(sessionId);
        if (sessionOpt.isEmpty()) {
            throw new RuntimeException("Session non trouvée: " + sessionId);
        }
        
        return chatMessageRepository.findBySessionIdOrderByCreatedAtAsc(sessionId);
    }
    
    /**
     * Mettre à jour le titre d'une session
     */
    public ChatSession updateSessionTitle(String sessionId, String title) {
        Optional<ChatSession> sessionOpt = findBySessionId(sessionId);
        if (sessionOpt.isEmpty()) {
            throw new RuntimeException("Session non trouvée: " + sessionId);
        }
        
        ChatSession session = sessionOpt.get();
        session.setTitle(title);
        session.setLastActivityAt(LocalDateTime.now());
        
        ChatSession savedSession = chatSessionRepository.save(session);
        log.info("📝 Titre de session mis à jour: {} -> {}", sessionId, title);
        
        return savedSession;
    }
    
    /**
     * Générer automatiquement un titre basé sur le premier message
     */
    public ChatSession generateSessionTitle(String sessionId) {
        List<ChatMessage> messages = getSessionMessages(sessionId);
        if (messages.isEmpty()) {
            return findBySessionId(sessionId).orElse(null);
        }
        
        // Prendre le premier message utilisateur pour générer le titre
        Optional<ChatMessage> firstUserMessage = messages.stream()
            .filter(ChatMessage::isFromUser)
            .findFirst();
        
        if (firstUserMessage.isPresent()) {
            String content = firstUserMessage.get().getContent();
            String title = content.length() > 50 ? content.substring(0, 50) + "..." : content;
            return updateSessionTitle(sessionId, title);
        }
        
        return findBySessionId(sessionId).orElse(null);
    }
    
    /**
     * Évaluer un message
     */
    public ChatMessage rateMessage(Long messageId, ChatMessage.MessageRating rating) {
        Optional<ChatMessage> messageOpt = chatMessageRepository.findById(messageId);
        if (messageOpt.isEmpty()) {
            throw new RuntimeException("Message non trouvé: " + messageId);
        }
        
        ChatMessage message = messageOpt.get();
        message.setRating(rating);
        
        ChatMessage savedMessage = chatMessageRepository.save(message);
        log.info("⭐ Message évalué: {} -> {}", messageId, rating);
        
        return savedMessage;
    }
    
    /**
     * Désactiver une session
     */
    public void deactivateSession(String sessionId) {
        chatSessionRepository.deactivateSession(sessionId);
        log.info("🔒 Session désactivée: {}", sessionId);
    }
    
    /**
     * Supprimer une session et tous ses messages
     */
    public void deleteSession(String sessionId) {
        Optional<ChatSession> sessionOpt = findBySessionId(sessionId);
        if (sessionOpt.isPresent()) {
            ChatSession session = sessionOpt.get();
            chatMessageRepository.deleteAll(session.getMessages());
            chatSessionRepository.delete(session);
            log.info("🗑️ Session supprimée: {}", sessionId);
        }
    }
    
    /**
     * Nettoyer les anciennes sessions inactives
     */
    public int cleanupInactiveSessions(int daysOld) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysOld);
        int deletedCount = chatSessionRepository.deleteInactiveSessionsOlderThan(cutoffDate);
        log.info("🧹 {} sessions inactives supprimées (plus de {} jours)", deletedCount, daysOld);
        return deletedCount;
    }
    
    /**
     * Obtenir les statistiques d'une session
     */
    @Transactional(readOnly = true)
    public SessionStats getSessionStats(String sessionId) {
        Optional<ChatSession> sessionOpt = findBySessionId(sessionId);
        if (sessionOpt.isEmpty()) {
            throw new RuntimeException("Session non trouvée: " + sessionId);
        }
        
        ChatSession session = sessionOpt.get();
        List<ChatMessage> messages = session.getMessages();
        
        SessionStats stats = new SessionStats();
        stats.setSessionId(sessionId);
        stats.setTotalMessages(messages.size());
        stats.setUserMessages((int) messages.stream().filter(ChatMessage::isFromUser).count());
        stats.setAiMessages((int) messages.stream().filter(ChatMessage::isFromAI).count());
        stats.setTotalTokens(messages.stream()
            .filter(ChatMessage::isFromAI)
            .mapToLong(msg -> msg.getTokens() != null ? msg.getTokens() : 0)
            .sum());
        stats.setAverageResponseTime(messages.stream()
            .filter(ChatMessage::isFromAI)
            .filter(msg -> msg.getResponseTime() != null)
            .mapToDouble(ChatMessage::getResponseTime)
            .average()
            .orElse(0.0));
        stats.setDuration(session.getFormattedDuration());
        stats.setCreatedAt(session.getCreatedAt());
        stats.setLastActivityAt(session.getLastActivityAt());
        
        return stats;
    }
    
    /**
     * Générer un ID de session unique
     */
    private String generateSessionId() {
        return "chat_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }

    /**
     * Supprimer toutes les sessions d'un utilisateur par ID
     */
    @Transactional
    public int deleteAllUserSessions(Long userId) {
        log.info("🗑️ Suppression de toutes les sessions pour userId: {}", userId);
        
        List<ChatSession> userSessions = chatSessionRepository.findByUserIdOrderByLastActivityAtDesc(userId);
        int deletedCount = userSessions.size();
        
        for (ChatSession session : userSessions) {
            // Supprimer tous les messages de la session
            chatMessageRepository.deleteByChatSessionId(session.getId());
            // Supprimer la session
            chatSessionRepository.delete(session);
        }
        
        log.info("✅ {} sessions supprimées pour userId: {}", deletedCount, userId);
        return deletedCount;
    }

    /**
     * Supprimer toutes les sessions d'un utilisateur par email
     */
    @Transactional
    public int deleteAllUserSessionsByEmail(String userEmail) {
        log.info("🗑️ Suppression de toutes les sessions pour userEmail: {}", userEmail);
        
        List<ChatSession> userSessions = chatSessionRepository.findByUserEmailOrderByLastActivityAtDesc(userEmail);
        int deletedCount = userSessions.size();
        
        for (ChatSession session : userSessions) {
            // Supprimer tous les messages de la session
            chatMessageRepository.deleteByChatSessionId(session.getId());
            // Supprimer la session
            chatSessionRepository.delete(session);
        }
        
        log.info("✅ {} sessions supprimées pour userEmail: {}", deletedCount, userEmail);
        return deletedCount;
    }
    
    /**
     * Classe pour les statistiques de session
     */
    public static class SessionStats {
        private String sessionId;
        private int totalMessages;
        private int userMessages;
        private int aiMessages;
        private long totalTokens;
        private double averageResponseTime;
        private String duration;
        private LocalDateTime createdAt;
        private LocalDateTime lastActivityAt;
        
        // Getters et setters
        public String getSessionId() { return sessionId; }
        public void setSessionId(String sessionId) { this.sessionId = sessionId; }
        
        public int getTotalMessages() { return totalMessages; }
        public void setTotalMessages(int totalMessages) { this.totalMessages = totalMessages; }
        
        public int getUserMessages() { return userMessages; }
        public void setUserMessages(int userMessages) { this.userMessages = userMessages; }
        
        public int getAiMessages() { return aiMessages; }
        public void setAiMessages(int aiMessages) { this.aiMessages = aiMessages; }
        
        public long getTotalTokens() { return totalTokens; }
        public void setTotalTokens(long totalTokens) { this.totalTokens = totalTokens; }
        
        public double getAverageResponseTime() { return averageResponseTime; }
        public void setAverageResponseTime(double averageResponseTime) { this.averageResponseTime = averageResponseTime; }
        
        public String getDuration() { return duration; }
        public void setDuration(String duration) { this.duration = duration; }
        
        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
        
        public LocalDateTime getLastActivityAt() { return lastActivityAt; }
        public void setLastActivityAt(LocalDateTime lastActivityAt) { this.lastActivityAt = lastActivityAt; }
    }
}
