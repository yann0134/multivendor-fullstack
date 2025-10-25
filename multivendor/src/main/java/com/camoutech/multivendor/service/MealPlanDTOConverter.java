package com.camoutech.multivendor.service;

import com.camoutech.multivendor.dto.MealPlanDTO;
import com.camoutech.multivendor.dto.MealPlanItemDTO;
import com.camoutech.multivendor.model.MealPlan;
import com.camoutech.multivendor.model.MealPlanItem;
import com.camoutech.multivendor.model.Recipe;
import com.camoutech.multivendor.model.RecipeIngredient;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service de conversion entre les entités et les DTOs pour les plannings de repas
 */
@Service
public class MealPlanDTOConverter {

    /**
     * Convertir un MealPlan en DTO
     */
    public MealPlanDTO toDTO(MealPlan mealPlan) {
        MealPlanDTO dto = new MealPlanDTO();
        dto.setId(mealPlan.getId());
        dto.setName(mealPlan.getName());
        dto.setDescription(mealPlan.getDescription());
        dto.setStartDate(mealPlan.getStartDate());
        dto.setEndDate(mealPlan.getEndDate());
        dto.setTotalDays(mealPlan.getTotalDays());
        dto.setServingsPerMeal(mealPlan.getServingsPerMeal());
        dto.setTotalPrice(mealPlan.getTotalPrice());
        dto.setMinPricePerRecipe(mealPlan.getMinPricePerRecipe());
        dto.setMealType(mealPlan.getMealType());
        dto.setCreatedAt(mealPlan.getCreatedAt());
        dto.setUpdatedAt(mealPlan.getUpdatedAt());
        
        // Convertir les items
        if (mealPlan.getItems() != null) {
            dto.setItems(mealPlan.getItems().stream()
                .map(this::toItemDTO)
                .collect(Collectors.toList()));
        }
        
        return dto;
    }

    /**
     * Convertir un MealPlanItem en DTO
     */
    public MealPlanItemDTO toItemDTO(MealPlanItem item) {
        MealPlanItemDTO dto = new MealPlanItemDTO();
        dto.setId(item.getId());
        dto.setMealDate(item.getMealDate());
        dto.setMealType(item.getMealType());
        dto.setServings(item.getServings());
        dto.setPriceForServings(item.getPriceForServings());
        
        // Récupérer les informations de la recette
        if (item.getRecipe() != null) {
            Recipe recipe = item.getRecipe();
            dto.setRecipeId(recipe.getId());
            dto.setRecipeTitle(recipe.getTitle());
            dto.setRecipeDescription(recipe.getDescription());
            
            // Récupérer les 2 premiers ingrédients
            List<RecipeIngredient> ingredients = recipe.getIngredients();
            if (ingredients != null && !ingredients.isEmpty()) {
                if (ingredients.size() >= 1) {
                    dto.setFirstIngredient(ingredients.get(0).getProduct().getTitle() + 
                                         " (" + ingredients.get(0).getQuantity() + " " + 
                                         ingredients.get(0).getUnit() + ")");
                }
                if (ingredients.size() >= 2) {
                    dto.setSecondIngredient(ingredients.get(1).getProduct().getTitle() + 
                                          " (" + ingredients.get(1).getQuantity() + " " + 
                                          ingredients.get(1).getUnit() + ")");
                }
            }
        }
        
        return dto;
    }
}
