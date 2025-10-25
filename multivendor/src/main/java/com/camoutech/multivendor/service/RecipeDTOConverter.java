package com.camoutech.multivendor.service;

import com.camoutech.multivendor.dto.RecipeDTO;
import com.camoutech.multivendor.dto.RecipeIngredientDTO;
import com.camoutech.multivendor.model.Recipe;
import com.camoutech.multivendor.model.RecipeIngredient;
import com.camoutech.multivendor.model.Product;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service pour convertir les entités Recipe en DTOs
 */
@Service
public class RecipeDTOConverter {
    
    public RecipeDTO toDTO(Recipe recipe) {
        RecipeDTO dto = new RecipeDTO();
        dto.setId(recipe.getId());
        dto.setTitle(recipe.getTitle());
        dto.setDescription(recipe.getDescription());
        dto.setInstructions(recipe.getInstructions());
        dto.setServings(recipe.getServings());
        dto.setPreparationTime(recipe.getPreparationTime());
        dto.setCookingTime(recipe.getCookingTime());
        dto.setDifficulty(recipe.getDifficulty());
        dto.setCategory(recipe.getCategory());
        dto.setIsPublished(recipe.getIsPublished());
        dto.setTotalPrice(recipe.getTotalPrice());
        dto.setUserFullName(recipe.getUser() != null ? recipe.getUser().getFullName() : "Utilisateur inconnu");
        dto.setCreatedAt(recipe.getCreatedAt());
        dto.setUpdatedAt(recipe.getUpdatedAt());
        
        if (recipe.getIngredients() != null) {
            dto.setIngredients(recipe.getIngredients().stream()
                .map(this::toIngredientDTO)
                .collect(Collectors.toList()));
        }
        
        return dto;
    }
    
    public RecipeIngredientDTO toIngredientDTO(RecipeIngredient ingredient) {
        RecipeIngredientDTO dto = new RecipeIngredientDTO();
        dto.setId(ingredient.getId());
        dto.setQuantity(ingredient.getQuantity());
        dto.setUnit(ingredient.getUnit());
        dto.setNotes(ingredient.getNotes());
        
        if (ingredient.getProduct() != null) {
            dto.setProductId(ingredient.getProduct().getId());
            dto.setProductTitle(ingredient.getProduct().getTitle());
            dto.setProductPrice(ingredient.getProduct().getSellingPrice());
            
            // Créer un objet product complet pour le frontend
            Product product = new Product();
            product.setId(ingredient.getProduct().getId());
            product.setTitle(ingredient.getProduct().getTitle());
            product.setSellingPrice(ingredient.getProduct().getSellingPrice());
            dto.setProduct(product);
        }
        
        return dto;
    }
}
