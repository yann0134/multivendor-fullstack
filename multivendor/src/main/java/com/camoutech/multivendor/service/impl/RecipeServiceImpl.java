package com.camoutech.multivendor.service.impl;

import com.camoutech.multivendor.model.Recipe;
import com.camoutech.multivendor.repository.RecipeRepository;
import com.camoutech.multivendor.service.RecipeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implémentation du service pour la gestion des recettes
 */
@Service
@RequiredArgsConstructor
public class RecipeServiceImpl implements RecipeService {
    
    private final RecipeRepository recipeRepository;
    
    @Override
    public Recipe findRecipeById(Long id) {
        return recipeRepository.findById(id).orElse(null);
    }
    
    @Override
    public Recipe saveRecipe(Recipe recipe) {
        return recipeRepository.save(recipe);
    }
    
    @Override
    public Recipe updateRecipe(Recipe recipe) {
        return recipeRepository.save(recipe);
    }
    
    @Override
    public void deleteRecipe(Long id) {
        recipeRepository.deleteById(id);
    }
    
    @Override
    public List<Recipe> findUserRecipes(Long userId) {
        return recipeRepository.findByUser_IdOrderByCreatedAtDesc(userId);
    }
    
    @Override
    public List<Recipe> findPublishedRecipes() {
        return recipeRepository.findByIsPublishedTrueOrderByCreatedAtDesc();
    }
}
