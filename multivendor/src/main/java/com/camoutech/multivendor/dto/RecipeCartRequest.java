package com.camoutech.multivendor.dto;

import lombok.Data;

/**
 * DTO pour l'ajout d'une recette au panier
 */
@Data
public class RecipeCartRequest {
    private Long recipeId;
    private Integer servings;
}
