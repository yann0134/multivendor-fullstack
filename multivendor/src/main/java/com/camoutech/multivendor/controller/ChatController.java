package com.camoutech.multivendor.controller;

import com.camoutech.multivendor.dto.ChatMessageDto;
import com.camoutech.multivendor.dto.ChatSessionDto;
import com.camoutech.multivendor.entity.ChatMessage;
import com.camoutech.multivendor.entity.ChatSession;
import com.camoutech.multivendor.service.ChatSessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class ChatController {
    
    private final ChatSessionService chatSessionService;
    
    /**
     * Créer une nouvelle session de chat
     */
    @PostMapping("/sessions")
    public ResponseEntity<ChatSessionDto> createSession(@RequestBody CreateSessionRequest request) {
        
        log.info("🆕 Création d'une nouvelle session pour userId: {}, email: {}", request.getUserId(), request.getUserEmail());
        
        ChatSession session = chatSessionService.createSession(request.getUserId(), request.getUserEmail());
        ChatSessionDto dto = convertToDto(session);
        
        return ResponseEntity.ok(dto);
    }
    
    /**
     * Récupérer une session par ID
     */
    @GetMapping("/sessions/{sessionId}")
    public ResponseEntity<ChatSessionDto> getSession(@PathVariable String sessionId) {
        log.info("📖 Récupération de la session: {}", sessionId);
        
        return chatSessionService.findBySessionId(sessionId)
            .map(session -> {
                ChatSessionDto dto = convertToDto(session);
                // Charger les messages
                List<ChatMessage> messages = chatSessionService.getSessionMessages(sessionId);
                dto.setMessages(messages.stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList()));
                return ResponseEntity.ok(dto);
            })
            .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Récupérer les sessions d'un utilisateur
     */
    @GetMapping("/sessions/user/{userId}")
    public ResponseEntity<List<ChatSessionDto>> getUserSessions(@PathVariable Long userId) {
        log.info("📋 Récupération des sessions pour userId: {}", userId);
        
        List<ChatSession> sessions = chatSessionService.findActiveSessionsByUserId(userId);
        List<ChatSessionDto> dtos = sessions.stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(dtos);
    }
    
    /**
     * Récupérer les sessions par email
     */
    @GetMapping("/sessions/email/{userEmail}")
    public ResponseEntity<List<ChatSessionDto>> getSessionsByEmail(@PathVariable String userEmail) {
        log.info("📋 Récupération des sessions pour email: {}", userEmail);
        
        List<ChatSession> sessions = chatSessionService.findActiveSessionsByEmail(userEmail);
        List<ChatSessionDto> dtos = sessions.stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(dtos);
    }
    
    /**
     * Récupérer les sessions avec pagination
     */
    @GetMapping("/sessions/user/{userId}/page")
    public ResponseEntity<Page<ChatSessionDto>> getUserSessionsPage(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        log.info("📄 Récupération paginée des sessions pour userId: {} (page: {}, size: {})", userId, page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<ChatSession> sessions = chatSessionService.findActiveSessionsByUserId(userId, pageable);
        Page<ChatSessionDto> dtos = sessions.map(this::convertToDto);
        
        return ResponseEntity.ok(dtos);
    }
    
    /**
     * Récupérer les messages d'une session
     */
    @GetMapping("/sessions/{sessionId}/messages")
    public ResponseEntity<List<ChatMessageDto>> getSessionMessages(@PathVariable String sessionId) {
        log.info("💬 Récupération des messages pour la session: {}", sessionId);
        
        List<ChatMessage> messages = chatSessionService.getSessionMessages(sessionId);
        List<ChatMessageDto> dtos = messages.stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(dtos);
    }
    
    /**
     * Ajouter un message à une session
     */
    @PostMapping("/sessions/{sessionId}/messages")
    public ResponseEntity<ChatMessageDto> addMessage(
            @PathVariable String sessionId,
            @RequestBody AddMessageRequest request) {
        
        log.info("➕ Ajout d'un message à la session: {} (utilisateur: {})", sessionId, request.getIsUser());
        
        ChatMessage message = chatSessionService.addMessage(
            sessionId,
            request.getContent(),
            request.getIsUser(),
            request.getResponseTime(),
            request.getTokens()
        );
        
        ChatMessageDto dto = convertToDto(message);
        return ResponseEntity.ok(dto);
    }
    
    /**
     * Mettre à jour le titre d'une session
     */
    @PutMapping("/sessions/{sessionId}/title")
    public ResponseEntity<ChatSessionDto> updateSessionTitle(
            @PathVariable String sessionId,
            @RequestBody UpdateTitleRequest request) {
        
        log.info("📝 Mise à jour du titre de la session: {} -> {}", sessionId, request.getTitle());
        
        ChatSession session = chatSessionService.updateSessionTitle(sessionId, request.getTitle());
        ChatSessionDto dto = convertToDto(session);
        
        return ResponseEntity.ok(dto);
    }
    
    /**
     * Générer automatiquement le titre d'une session
     */
    @PostMapping("/sessions/{sessionId}/generate-title")
    public ResponseEntity<ChatSessionDto> generateSessionTitle(@PathVariable String sessionId) {
        log.info("🤖 Génération automatique du titre pour la session: {}", sessionId);
        
        ChatSession session = chatSessionService.generateSessionTitle(sessionId);
        if (session != null) {
            ChatSessionDto dto = convertToDto(session);
            return ResponseEntity.ok(dto);
        }
        
        return ResponseEntity.notFound().build();
    }
    
    /**
     * Évaluer un message
     */
    @PostMapping("/messages/{messageId}/rate")
    public ResponseEntity<ChatMessageDto> rateMessage(
            @PathVariable Long messageId,
            @RequestBody RateMessageRequest request) {
        
        log.info("⭐ Évaluation du message: {} -> {}", messageId, request.getRating());
        
        ChatMessage message = chatSessionService.rateMessage(messageId, request.getRating());
        ChatMessageDto dto = convertToDto(message);
        
        return ResponseEntity.ok(dto);
    }
    
    /**
     * Désactiver une session
     */
    @DeleteMapping("/sessions/{sessionId}")
    public ResponseEntity<Void> deactivateSession(@PathVariable String sessionId) {
        log.info("🔒 Désactivation de la session: {}", sessionId);
        
        chatSessionService.deactivateSession(sessionId);
        return ResponseEntity.ok().build();
    }
    
    /**
     * Supprimer définitivement une session
     */
    @DeleteMapping("/sessions/{sessionId}/permanent")
    public ResponseEntity<Void> deleteSession(@PathVariable String sessionId) {
        log.info("🗑️ Suppression définitive de la session: {}", sessionId);
        
        chatSessionService.deleteSession(sessionId);
        return ResponseEntity.ok().build();
    }
    
    /**
     * Obtenir les statistiques d'une session
     */
    @GetMapping("/sessions/{sessionId}/stats")
    public ResponseEntity<ChatSessionService.SessionStats> getSessionStats(@PathVariable String sessionId) {
        log.info("📊 Récupération des statistiques pour la session: {}", sessionId);
        
        ChatSessionService.SessionStats stats = chatSessionService.getSessionStats(sessionId);
        return ResponseEntity.ok(stats);
    }
    
    /**
     * Nettoyer les anciennes sessions
     */
    @PostMapping("/cleanup")
    public ResponseEntity<Integer> cleanupSessions(@RequestParam(defaultValue = "30") int daysOld) {
        log.info("🧹 Nettoyage des sessions plus anciennes que {} jours", daysOld);
        
        int deletedCount = chatSessionService.cleanupInactiveSessions(daysOld);
        return ResponseEntity.ok(deletedCount);
    }

    /**
     * Supprimer toutes les sessions d'un utilisateur
     */
    @DeleteMapping("/sessions/user/{userId}/all")
    public ResponseEntity<Map<String, Object>> deleteAllUserSessions(@PathVariable Long userId) {
        log.info("🗑️ Suppression de toutes les sessions pour userId: {}", userId);
        
        try {
            int deletedCount = chatSessionService.deleteAllUserSessions(userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Toutes les conversations ont été supprimées avec succès");
            response.put("deletedSessions", deletedCount);
            response.put("userId", userId);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Erreur lors de la suppression des sessions pour userId {}: {}", userId, e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Erreur lors de la suppression des conversations");
            errorResponse.put("message", e.getMessage());
            
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    /**
     * Supprimer toutes les sessions d'un utilisateur par email
     */
    @DeleteMapping("/sessions/email/{userEmail}/all")
    public ResponseEntity<Map<String, Object>> deleteAllUserSessionsByEmail(@PathVariable String userEmail) {
        log.info("🗑️ Suppression de toutes les sessions pour userEmail: {}", userEmail);
        
        try {
            int deletedCount = chatSessionService.deleteAllUserSessionsByEmail(userEmail);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Toutes les conversations ont été supprimées avec succès");
            response.put("deletedSessions", deletedCount);
            response.put("userEmail", userEmail);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Erreur lors de la suppression des sessions pour userEmail {}: {}", userEmail, e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Erreur lors de la suppression des conversations");
            errorResponse.put("message", e.getMessage());
            
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
    
    // Méthodes de conversion
    private ChatSessionDto convertToDto(ChatSession session) {
        ChatSessionDto dto = new ChatSessionDto();
        dto.setId(session.getId());
        dto.setSessionId(session.getSessionId());
        dto.setUserId(session.getUserId());
        dto.setUserEmail(session.getUserEmail());
        dto.setTitle(session.getTitle());
        dto.setIsActive(session.getIsActive());
        dto.setMessageCount(session.getMessageCount());
        dto.setTotalTokens(session.getTotalTokens());
        dto.setAverageResponseTime(session.getAverageResponseTime());
        dto.setCreatedAt(session.getCreatedAt());
        dto.setUpdatedAt(session.getUpdatedAt());
        dto.setLastActivityAt(session.getLastActivityAt());
        dto.setDuration(session.getFormattedDuration());
        return dto;
    }
    
    private ChatMessageDto convertToDto(ChatMessage message) {
        ChatMessageDto dto = new ChatMessageDto();
        dto.setId(message.getId());
        dto.setContent(message.getContent());
        dto.setIsUser(message.getIsUser());
        dto.setResponseTime(message.getResponseTime());
        dto.setTokens(message.getTokens());
        dto.setRating(message.getRating() != null ? message.getRating().name() : null);
        dto.setMessageOrder(message.getMessageOrder());
        dto.setCreatedAt(message.getCreatedAt());
        dto.setFormattedTime(message.getFormattedTime());
        dto.setFormattedDate(message.getFormattedDate());
        return dto;
    }
    
    // Classes de requête
    @lombok.Data
    public static class CreateSessionRequest {
        private Long userId;
        private String userEmail;
    }
    
    @lombok.Data
    public static class AddMessageRequest {
        private String content;
        private Boolean isUser;
        private Double responseTime;
        private Long tokens;
    }
    
    @lombok.Data
    public static class UpdateTitleRequest {
        private String title;
    }
    
    @lombok.Data
    public static class RateMessageRequest {
        private ChatMessage.MessageRating rating;
    }
}
