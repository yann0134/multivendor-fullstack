package com.camoutech.multivendor.dto;

import com.camoutech.multivendor.model.Product;
import lombok.Data;

/**
 * DTO pour les ingrédients de recettes
 */
@Data
public class RecipeIngredientDTO {
    private Long id;
    private Long productId;
    private String productTitle;
    private Integer productPrice;
    private Product product; // Objet product complet pour le frontend
    private Integer quantity;
    private String unit;
    private String notes;
}
