package com.camoutech.multivendor.repository;

import com.camoutech.multivendor.model.Recipe;
import com.camoutech.multivendor.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository pour les recettes
 */
@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {
    
    // Récupérer les recettes d'un utilisateur
    Page<Recipe> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);
    
    // Récupérer les recettes publiques
    Page<Recipe> findByIsPublishedTrueOrderByCreatedAtDesc(Pageable pageable);
    
    // Récupérer les recettes par catégorie
    Page<Recipe> findByCategoryAndIsPublishedTrueOrderByCreatedAtDesc(String category, Pageable pageable);
    
    // Récupérer les recettes par difficulté
    Page<Recipe> findByDifficultyAndIsPublishedTrueOrderByCreatedAtDesc(String difficulty, Pageable pageable);
    
    // Rechercher des recettes par titre
    @Query("SELECT r FROM Recipe r WHERE r.isPublished = true AND " +
           "(LOWER(r.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(r.description) LIKE LOWER(CONCAT('%', :query, '%'))) " +
           "ORDER BY r.createdAt DESC")
    Page<Recipe> searchPublishedRecipes(@Param("query") String query, Pageable pageable);
    
    // Récupérer les recettes populaires (avec le plus d'ingrédients)
    @Query("SELECT r FROM Recipe r WHERE r.isPublished = true " +
           "ORDER BY SIZE(r.ingredients) DESC, r.createdAt DESC")
    Page<Recipe> findPopularRecipes(Pageable pageable);
    
    // Récupérer les recettes récentes
    @Query("SELECT r FROM Recipe r WHERE r.isPublished = true " +
           "ORDER BY r.createdAt DESC")
    Page<Recipe> findRecentRecipes(Pageable pageable);
    
    // Récupérer les recettes par prix maximum
    @Query("SELECT r FROM Recipe r WHERE r.isPublished = true AND r.totalPrice <= :maxPrice " +
           "ORDER BY r.totalPrice ASC, r.createdAt DESC")
    Page<Recipe> findByMaxPrice(@Param("maxPrice") Integer maxPrice, Pageable pageable);
    
    // Récupérer les recettes par temps de préparation maximum
    @Query("SELECT r FROM Recipe r WHERE r.isPublished = true AND " +
           "(r.preparationTime + r.cookingTime) <= :maxTime " +
           "ORDER BY (r.preparationTime + r.cookingTime) ASC, r.createdAt DESC")
    Page<Recipe> findByMaxTime(@Param("maxTime") Integer maxTime, Pageable pageable);
    
    // Compter les recettes d'un utilisateur
    Long countByUser(User user);
    
    // Compter les recettes publiques
    Long countByIsPublishedTrue();
    
    // Récupérer les recettes d'un utilisateur par ID
    List<Recipe> findByUser_IdOrderByCreatedAtDesc(Long userId);
    
    // Récupérer les recettes publiques (sans pagination)
    List<Recipe> findByIsPublishedTrueOrderByCreatedAtDesc();
    
    // Récupérer une recette avec ses ingrédients et leurs produits
    @Query("SELECT DISTINCT r FROM Recipe r LEFT JOIN FETCH r.ingredients i LEFT JOIN FETCH i.product WHERE r.id = :id")
    Recipe findByIdWithIngredientsAndProducts(@Param("id") Long id);
}
