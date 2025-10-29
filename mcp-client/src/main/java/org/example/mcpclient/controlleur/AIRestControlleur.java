package org.example.mcpclient.controlleur;

import org.example.mcpclient.agent.AiAgent;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.HashMap;

@RestController
@CrossOrigin(origins = "*")
public class AIRestControlleur {
    
    private static final Logger log = LoggerFactory.getLogger(AIRestControlleur.class);
    private final AiAgent agent;

    public AIRestControlleur(AiAgent agent) {
        this.agent = agent;
    }

    @GetMapping("/chat")
    public String chat(@RequestParam String query) {
        log.info("🤖 Requête chat reçue: {}", query);
        long startTime = System.currentTimeMillis();
        
        try {
            String response = agent.askLLM(query);
            long responseTime = System.currentTimeMillis() - startTime;
            
            log.info("✅ Réponse générée en {}ms", responseTime);
            return response;
        } catch (Exception e) {
            log.error("❌ Erreur lors de la génération de la réponse: {}", e.getMessage());
            return "Désolé, une erreur s'est produite lors de la génération de la réponse.";
        }
    }
    
    @PostMapping("/chat/session")
    public ResponseEntity<Map<String, Object>> chatWithSession(@RequestBody ChatRequest request) {
        log.info("🤖 Requête chat avec session: {} (sessionId: {})", request.getQuery(), request.getSessionId());
        
        long startTime = System.currentTimeMillis();
        
        try {
            String response = agent.askLLM(request.getQuery());
            long responseTime = System.currentTimeMillis() - startTime;
            
            // Préparer la réponse avec métadonnées
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("response", response);
            responseData.put("sessionId", request.getSessionId());
            responseData.put("responseTime", responseTime);
            responseData.put("timestamp", LocalDateTime.now());
            responseData.put("tokens", estimateTokens(response));
            
            log.info("✅ Réponse générée en {}ms pour la session {}", responseTime, request.getSessionId());
            
            return ResponseEntity.ok(responseData);
        } catch (Exception e) {
            log.error("❌ Erreur lors de la génération de la réponse: {}", e.getMessage());
            
            Map<String, Object> errorData = new HashMap<>();
            errorData.put("error", "Erreur lors de la génération de la réponse");
            errorData.put("message", e.getMessage());
            errorData.put("sessionId", request.getSessionId());
            errorData.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.internalServerError().body(errorData);
        }
    }
    
    @GetMapping("/chat/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", LocalDateTime.now());
        health.put("service", "AI Chat Service");
        
        return ResponseEntity.ok(health);
    }
    
    /**
     * Estimation approximative du nombre de tokens
     */
    private long estimateTokens(String text) {
        // Estimation approximative: 1 token ≈ 4 caractères
        return Math.max(1, text.length() / 4);
    }
    
    // Classe de requête
    public static class ChatRequest {
        private String query;
        private String sessionId;
        private Long userId;
        private String userEmail;
        
        // Constructeurs
        public ChatRequest() {}
        
        public ChatRequest(String query, String sessionId) {
            this.query = query;
            this.sessionId = sessionId;
        }
        
        // Getters et setters
        public String getQuery() { return query; }
        public void setQuery(String query) { this.query = query; }
        
        public String getSessionId() { return sessionId; }
        public void setSessionId(String sessionId) { this.sessionId = sessionId; }
        
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        
        public String getUserEmail() { return userEmail; }
        public void setUserEmail(String userEmail) { this.userEmail = userEmail; }
    }
}
