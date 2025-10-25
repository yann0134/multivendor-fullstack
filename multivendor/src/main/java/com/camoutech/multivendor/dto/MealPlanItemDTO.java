package com.camoutech.multivendor.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO pour les items d'un planning de repas
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MealPlanItemDTO {
    
    private Long id;
    private Long recipeId;
    private String recipeTitle;
    private String recipeDescription;
    private String firstIngredient;
    private String secondIngredient;
    private LocalDate mealDate;
    private String mealType;
    private Integer servings;
    private Integer priceForServings;
}
