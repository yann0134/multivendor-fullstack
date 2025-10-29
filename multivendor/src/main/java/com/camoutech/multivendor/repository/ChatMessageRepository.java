package com.camoutech.multivendor.repository;

import com.camoutech.multivendor.entity.ChatMessage;
import com.camoutech.multivendor.entity.ChatSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    
    // Trouver tous les messages d'une session
    List<ChatMessage> findByChatSessionOrderByCreatedAtAsc(ChatSession chatSession);
    
    // Trouver tous les messages d'une session avec pagination
    Page<ChatMessage> findByChatSessionOrderByCreatedAtAsc(ChatSession chatSession, Pageable pageable);
    
    // Trouver les messages d'une session par sessionId
    @Query("SELECT cm FROM ChatMessage cm WHERE cm.chatSession.sessionId = :sessionId ORDER BY cm.createdAt ASC")
    List<ChatMessage> findBySessionIdOrderByCreatedAtAsc(@Param("sessionId") String sessionId);
    
    // Trouver les messages d'un utilisateur
    @Query("SELECT cm FROM ChatMessage cm WHERE cm.chatSession.userId = :userId ORDER BY cm.createdAt DESC")
    List<ChatMessage> findByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId, Pageable pageable);
    
    // Trouver les messages par email utilisateur
    @Query("SELECT cm FROM ChatMessage cm WHERE cm.chatSession.userEmail = :userEmail ORDER BY cm.createdAt DESC")
    List<ChatMessage> findByUserEmailOrderByCreatedAtDesc(@Param("userEmail") String userEmail, Pageable pageable);
    
    // Trouver les messages créés dans une période donnée
    List<ChatMessage> findByCreatedAtBetweenOrderByCreatedAtDesc(LocalDateTime startDate, LocalDateTime endDate);
    
    // Trouver les messages avec une évaluation spécifique
    List<ChatMessage> findByRatingOrderByCreatedAtDesc(ChatMessage.MessageRating rating);
    
    // Trouver les messages de l'IA avec une évaluation positive
    @Query("SELECT cm FROM ChatMessage cm WHERE cm.isUser = false AND cm.rating = 'POSITIVE' ORDER BY cm.createdAt DESC")
    List<ChatMessage> findPositiveAIMessages(Pageable pageable);
    
    // Trouver les messages de l'IA avec une évaluation négative
    @Query("SELECT cm FROM ChatMessage cm WHERE cm.isUser = false AND cm.rating = 'NEGATIVE' ORDER BY cm.createdAt DESC")
    List<ChatMessage> findNegativeAIMessages(Pageable pageable);
    
    // Compter les messages d'une session
    long countByChatSession(ChatSession chatSession);
    
    // Compter les messages d'un utilisateur
    @Query("SELECT COUNT(cm) FROM ChatMessage cm WHERE cm.chatSession.userId = :userId")
    long countByUserId(@Param("userId") Long userId);
    
    // Compter les messages par email
    @Query("SELECT COUNT(cm) FROM ChatMessage cm WHERE cm.chatSession.userEmail = :userEmail")
    long countByUserEmail(@Param("userEmail") String userEmail);
    
    // Compter les messages de l'IA
    @Query("SELECT COUNT(cm) FROM ChatMessage cm WHERE cm.isUser = false")
    long countAIMessages();
    
    // Compter les messages utilisateur
    @Query("SELECT COUNT(cm) FROM ChatMessage cm WHERE cm.isUser = true")
    long countUserMessages();
    
    // Trouver le dernier message d'une session
    @Query("SELECT cm FROM ChatMessage cm WHERE cm.chatSession = :chatSession ORDER BY cm.createdAt DESC LIMIT 1")
    ChatMessage findLastMessageBySession(@Param("chatSession") ChatSession chatSession);
    
    // Trouver les messages contenant un texte spécifique
    @Query("SELECT cm FROM ChatMessage cm WHERE cm.content LIKE %:searchText% ORDER BY cm.createdAt DESC")
    List<ChatMessage> findByContentContaining(@Param("searchText") String searchText, Pageable pageable);
    
    // Trouver les messages d'une session contenant un texte
    @Query("SELECT cm FROM ChatMessage cm WHERE cm.chatSession = :chatSession AND cm.content LIKE %:searchText% ORDER BY cm.createdAt ASC")
    List<ChatMessage> findBySessionAndContentContaining(@Param("chatSession") ChatSession chatSession, @Param("searchText") String searchText);
    
    // Statistiques de performance
    @Query("SELECT AVG(cm.responseTime) FROM ChatMessage cm WHERE cm.isUser = false AND cm.responseTime IS NOT NULL")
    Double getAverageResponseTime();
    
    @Query("SELECT SUM(cm.tokens) FROM ChatMessage cm WHERE cm.isUser = false AND cm.tokens IS NOT NULL")
    Long getTotalTokens();
    
    // Nettoyer les anciens messages
    @Query("DELETE FROM ChatMessage cm WHERE cm.createdAt < :cutoffDate")
    int deleteMessagesOlderThan(@Param("cutoffDate") LocalDateTime cutoffDate);
    
    // Trouver les messages avec le temps de réponse le plus élevé
    @Query("SELECT cm FROM ChatMessage cm WHERE cm.isUser = false AND cm.responseTime IS NOT NULL ORDER BY cm.responseTime DESC")
    List<ChatMessage> findSlowestMessages(Pageable pageable);
    
    // Trouver les messages avec le temps de réponse le plus bas
    @Query("SELECT cm FROM ChatMessage cm WHERE cm.isUser = false AND cm.responseTime IS NOT NULL ORDER BY cm.responseTime ASC")
    List<ChatMessage> findFastestMessages(Pageable pageable);
    
    // Supprimer tous les messages d'une session par ID de session
    @Modifying
    @Query("DELETE FROM ChatMessage cm WHERE cm.chatSession.id = :sessionId")
    void deleteByChatSessionId(@Param("sessionId") Long sessionId);
}
