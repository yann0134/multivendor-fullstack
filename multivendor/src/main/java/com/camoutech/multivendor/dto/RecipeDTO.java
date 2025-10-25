package com.camoutech.multivendor.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO pour les recettes (sans les relations problématiques)
 */
@Data
public class RecipeDTO {
    private Long id;
    private String title;
    private String description;
    private String instructions;
    private Integer servings;
    private Integer preparationTime;
    private Integer cookingTime;
    private String difficulty;
    private String category;
    private Boolean isPublished;
    private Integer totalPrice;
    private String userFullName; // Nom de l'utilisateur au lieu de l'objet User
    private List<RecipeIngredientDTO> ingredients;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
