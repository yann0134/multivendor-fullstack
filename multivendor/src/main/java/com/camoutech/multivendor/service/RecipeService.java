package com.camoutech.multivendor.service;

import com.camoutech.multivendor.model.Recipe;

import java.util.List;

/**
 * Service pour la gestion des recettes
 */
public interface RecipeService {
    Recipe findRecipeById(Long id);
    Recipe saveRecipe(Recipe recipe);
    Recipe updateRecipe(Recipe recipe);
    void deleteRecipe(Long id);
    List<Recipe> findUserRecipes(Long userId);
    List<Recipe> findPublishedRecipes();
}
