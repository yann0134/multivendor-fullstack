package com.camoutech.multivendor.repository;

import com.camoutech.multivendor.entity.ChatSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ChatSessionRepository extends JpaRepository<ChatSession, Long> {
    
    // Trouver une session par sessionId
    Optional<ChatSession> findBySessionId(String sessionId);
    
    // Trouver les sessions actives d'un utilisateur
    List<ChatSession> findByUserIdAndIsActiveTrueOrderByLastActivityAtDesc(Long userId);
    
    // Trouver les sessions par email utilisateur
    List<ChatSession> findByUserEmailAndIsActiveTrueOrderByLastActivityAtDesc(String userEmail);
    
    // Trouver les sessions récentes d'un utilisateur avec pagination
    Page<ChatSession> findByUserIdAndIsActiveTrueOrderByLastActivityAtDesc(Long userId, Pageable pageable);
    
    // Trouver les sessions récentes par email avec pagination
    Page<ChatSession> findByUserEmailAndIsActiveTrueOrderByLastActivityAtDesc(String userEmail, Pageable pageable);
    
    // Compter les sessions actives d'un utilisateur
    long countByUserIdAndIsActiveTrue(Long userId);
    
    // Compter les sessions par email
    long countByUserEmailAndIsActiveTrue(String userEmail);
    
    // Trouver les sessions créées dans une période donnée
    List<ChatSession> findByCreatedAtBetweenAndIsActiveTrue(LocalDateTime startDate, LocalDateTime endDate);
    
    // Trouver les sessions avec le plus de messages
    @Query("SELECT cs FROM ChatSession cs WHERE cs.isActive = true ORDER BY cs.messageCount DESC")
    List<ChatSession> findMostActiveSessions(Pageable pageable);
    
    // Trouver les sessions d'un utilisateur avec un titre contenant le texte
    List<ChatSession> findByUserIdAndIsActiveTrueAndTitleContainingIgnoreCaseOrderByLastActivityAtDesc(
        Long userId, String title);
    
    // Trouver les sessions par email avec un titre contenant le texte
    List<ChatSession> findByUserEmailAndIsActiveTrueAndTitleContainingIgnoreCaseOrderByLastActivityAtDesc(
        String userEmail, String title);
    
    // Statistiques globales
    @Query("SELECT COUNT(cs) FROM ChatSession cs WHERE cs.isActive = true")
    long countActiveSessions();
    
    @Query("SELECT SUM(cs.messageCount) FROM ChatSession cs WHERE cs.isActive = true")
    Long getTotalMessages();
    
    @Query("SELECT AVG(cs.averageResponseTime) FROM ChatSession cs WHERE cs.isActive = true AND cs.averageResponseTime > 0")
    Double getAverageResponseTime();
    
    // Nettoyer les anciennes sessions inactives
    @Query("DELETE FROM ChatSession cs WHERE cs.isActive = false AND cs.updatedAt < :cutoffDate")
    int deleteInactiveSessionsOlderThan(@Param("cutoffDate") LocalDateTime cutoffDate);
    
    // Marquer une session comme inactive
    @Query("UPDATE ChatSession cs SET cs.isActive = false WHERE cs.sessionId = :sessionId")
    int deactivateSession(@Param("sessionId") String sessionId);
    
    // Trouver toutes les sessions d'un utilisateur (actives et inactives) pour suppression
    List<ChatSession> findByUserIdOrderByLastActivityAtDesc(Long userId);
    
    // Trouver toutes les sessions d'un utilisateur par email (actives et inactives) pour suppression
    List<ChatSession> findByUserEmailOrderByLastActivityAtDesc(String userEmail);
}
