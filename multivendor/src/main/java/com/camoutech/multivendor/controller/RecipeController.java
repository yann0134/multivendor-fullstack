package com.camoutech.multivendor.controller;

import com.camoutech.multivendor.dto.RecipeDTO;
import com.camoutech.multivendor.model.Recipe;
import com.camoutech.multivendor.model.RecipeIngredient;
import com.camoutech.multivendor.model.User;
import com.camoutech.multivendor.repository.RecipeRepository;
import com.camoutech.multivendor.service.RecipeDTOConverter;
import com.camoutech.multivendor.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Contrôleur pour la gestion des recettes
 */
@RestController
@RequestMapping("/api/recipes")
@RequiredArgsConstructor
public class RecipeController {
    
    private final RecipeRepository recipeRepository;
    private final UserService userService;
    private final RecipeDTOConverter dtoConverter;
    
    /**
     * Créer une nouvelle recette
     */
    @PostMapping
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public ResponseEntity<RecipeDTO> createRecipe(
            @RequestBody Recipe recipe,
            @RequestHeader("Authorization") String jwt) throws Exception {
        
        // Validation des champs requis
        if (recipe.getTitle() == null || recipe.getTitle().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        if (recipe.getInstructions() == null || recipe.getInstructions().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        if (recipe.getServings() == null || recipe.getServings() <= 0) {
            recipe.setServings(1);
        }
        
        if (recipe.getPreparationTime() == null || recipe.getPreparationTime() <= 0) {
            return ResponseEntity.badRequest().build();
        }
        
        if (recipe.getCookingTime() == null || recipe.getCookingTime() < 0) {
            recipe.setCookingTime(0);
        }
        
        if (recipe.getDifficulty() == null || recipe.getDifficulty().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        if (recipe.getCategory() == null || recipe.getCategory().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        User user = userService.findUserByJwtToken(jwt);
        recipe.setUser(user);
        
        // S'assurer que les ingrédients sont liés à la recette et ont des produits valides
        if (recipe.getIngredients() != null) {
            // Filtrer les ingrédients valides
            List<RecipeIngredient> validIngredients = recipe.getIngredients().stream()
                .filter(ingredient -> ingredient.getProduct() != null && ingredient.getProduct().getId() != null)
                .collect(java.util.stream.Collectors.toList());
            
            // Remplacer la liste des ingrédients par les ingrédients valides
            recipe.getIngredients().clear();
            recipe.getIngredients().addAll(validIngredients);
            
            // Lier les ingrédients à la recette
            for (RecipeIngredient ingredient : recipe.getIngredients()) {
                ingredient.setRecipe(recipe);
            }
        }
        
        // Sauvegarder la recette (les ingrédients seront sauvegardés automatiquement grâce à CascadeType.ALL)
        Recipe savedRecipe = recipeRepository.save(recipe);
        
        // Convertir en DTO avant de retourner
        RecipeDTO recipeDTO = dtoConverter.toDTO(savedRecipe);
        
        return new ResponseEntity<>(recipeDTO, HttpStatus.CREATED);
    }
    
    /**
     * Récupérer les recettes de l'utilisateur connecté
     */
    @GetMapping("/my-recipes")
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public ResponseEntity<Page<RecipeDTO>> getMyRecipes(
            @RequestHeader("Authorization") String jwt,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) throws Exception {
        
        User user = userService.findUserByJwtToken(jwt);
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Recipe> recipes = recipeRepository.findByUserOrderByCreatedAtDesc(user, pageable);
        
        // Convertir en DTOs
        Page<RecipeDTO> recipeDTOs = recipes.map(dtoConverter::toDTO);
        
        return ResponseEntity.ok(recipeDTOs);
    }
    
    /**
     * Récupérer toutes les recettes publiques
     */
    @GetMapping("/public")
    public ResponseEntity<Page<RecipeDTO>> getPublicRecipes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "recent") String sortBy) {
        
        Pageable pageable;
        Page<Recipe> recipes;
        
        switch (sortBy) {
            case "popular":
                pageable = PageRequest.of(page, size);
                recipes = recipeRepository.findPopularRecipes(pageable);
                break;
            case "price":
                pageable = PageRequest.of(page, size, Sort.by("totalPrice").ascending());
                recipes = recipeRepository.findByIsPublishedTrueOrderByCreatedAtDesc(pageable);
                break;
            default:
                pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
                recipes = recipeRepository.findByIsPublishedTrueOrderByCreatedAtDesc(pageable);
        }
        
        // Convertir en DTOs
        Page<RecipeDTO> recipeDTOs = recipes.map(dtoConverter::toDTO);
        
        return ResponseEntity.ok(recipeDTOs);
    }
    
    /**
     * Récupérer une recette par ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<RecipeDTO> getRecipeById(@PathVariable Long id) {
        Recipe recipe = recipeRepository.findByIdWithIngredientsAndProducts(id);
        if (recipe == null) {
            // Fallback: essayer avec la méthode standard
            recipe = recipeRepository.findById(id).orElse(null);
        }
        
        if (recipe != null) {
            System.out.println("Recipe trouvée: " + recipe.getTitle());
            System.out.println("Nombre d'ingrédients: " + (recipe.getIngredients() != null ? recipe.getIngredients().size() : 0));
            if (recipe.getIngredients() != null && !recipe.getIngredients().isEmpty()) {
                System.out.println("Premier ingrédient: " + recipe.getIngredients().get(0));
            }
            return ResponseEntity.ok(dtoConverter.toDTO(recipe));
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Mettre à jour une recette
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public ResponseEntity<RecipeDTO> updateRecipe(
            @PathVariable Long id,
            @RequestBody Recipe recipe,
            @RequestHeader("Authorization") String jwt) throws Exception {
        
        User user = userService.findUserByJwtToken(jwt);
        
        Optional<Recipe> existingRecipeOpt = recipeRepository.findById(id);
        if (existingRecipeOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        Recipe existingRecipe = existingRecipeOpt.get();
        if (!existingRecipe.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        existingRecipe.setTitle(recipe.getTitle());
        existingRecipe.setDescription(recipe.getDescription());
        existingRecipe.setInstructions(recipe.getInstructions());
        existingRecipe.setServings(recipe.getServings());
        existingRecipe.setPreparationTime(recipe.getPreparationTime());
        existingRecipe.setCookingTime(recipe.getCookingTime());
        existingRecipe.setDifficulty(recipe.getDifficulty());
        existingRecipe.setCategory(recipe.getCategory());
        existingRecipe.setIsPublished(recipe.getIsPublished());
        
        // Mettre à jour les ingrédients
        if (recipe.getIngredients() != null) {
            // Supprimer les anciens ingrédients
            existingRecipe.getIngredients().clear();
            
            // Ajouter les nouveaux ingrédients
            for (RecipeIngredient ingredient : recipe.getIngredients()) {
                // Vérifier que l'ingrédient a un produit valide
                if (ingredient.getProduct() != null && ingredient.getProduct().getId() != null) {
                    ingredient.setRecipe(existingRecipe);
                    existingRecipe.addIngredient(ingredient);
                }
            }
        }
        
        // Recalculer le prix total
        existingRecipe.calculateTotalPrice();
        
        Recipe updatedRecipe = recipeRepository.save(existingRecipe);
        RecipeDTO recipeDTO = dtoConverter.toDTO(updatedRecipe);
        return ResponseEntity.ok(recipeDTO);
    }
    
    /**
     * Supprimer une recette
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public ResponseEntity<Void> deleteRecipe(
            @PathVariable Long id,
            @RequestHeader("Authorization") String jwt) throws Exception {
        
        User user = userService.findUserByJwtToken(jwt);
        
        Optional<Recipe> recipeOpt = recipeRepository.findById(id);
        if (recipeOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        Recipe recipe = recipeOpt.get();
        if (!recipe.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        recipeRepository.delete(recipe);
        return ResponseEntity.ok().build();
    }
    
    /**
     * Publier/Dépublier une recette
     */
    @PutMapping("/{id}/publish")
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public ResponseEntity<RecipeDTO> togglePublishRecipe(
            @PathVariable Long id,
            @RequestHeader("Authorization") String jwt) throws Exception {
        
        User user = userService.findUserByJwtToken(jwt);
        
        Optional<Recipe> recipeOpt = recipeRepository.findById(id);
        if (recipeOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        Recipe recipe = recipeOpt.get();
        if (!recipe.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        recipe.setIsPublished(!recipe.getIsPublished());
        Recipe updatedRecipe = recipeRepository.save(recipe);
        RecipeDTO recipeDTO = dtoConverter.toDTO(updatedRecipe);
        return ResponseEntity.ok(recipeDTO);
    }
    
    /**
     * Rechercher des recettes publiques
     */
    @GetMapping("/search")
    public ResponseEntity<Page<RecipeDTO>> searchRecipes(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Recipe> recipes = recipeRepository.searchPublishedRecipes(query, pageable);
        Page<RecipeDTO> recipeDTOs = recipes.map(dtoConverter::toDTO);
        
        return ResponseEntity.ok(recipeDTOs);
    }
    
    /**
     * Filtrer les recettes publiques
     */
    @GetMapping("/filter")
    public ResponseEntity<Page<RecipeDTO>> filterRecipes(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String difficulty,
            @RequestParam(required = false) Integer maxPrice,
            @RequestParam(required = false) Integer maxTime,
            @RequestParam(defaultValue = "recent") String sortBy,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        
        Pageable pageable;
        Page<Recipe> recipes;
        
        // Gérer le tri selon le paramètre sortBy
        switch (sortBy) {
            case "popular":
                pageable = PageRequest.of(page, size);
                break;
            case "price":
                pageable = PageRequest.of(page, size, Sort.by("totalPrice").ascending());
                break;
            case "recent":
            default:
                pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
                break;
        }
        
        if (category != null) {
            recipes = recipeRepository.findByCategoryAndIsPublishedTrueOrderByCreatedAtDesc(category, pageable);
        } else if (difficulty != null) {
            recipes = recipeRepository.findByDifficultyAndIsPublishedTrueOrderByCreatedAtDesc(difficulty, pageable);
        } else if (maxPrice != null) {
            recipes = recipeRepository.findByMaxPrice(maxPrice, pageable);
        } else if (maxTime != null) {
            recipes = recipeRepository.findByMaxTime(maxTime, pageable);
        } else {
            recipes = recipeRepository.findByIsPublishedTrueOrderByCreatedAtDesc(pageable);
        }
        
        Page<RecipeDTO> recipeDTOs = recipes.map(dtoConverter::toDTO);
        return ResponseEntity.ok(recipeDTOs);
    }
    
    /**
     * Calculer le prix d'une recette pour un nombre de portions donné
     */
    @GetMapping("/{id}/price")
    public ResponseEntity<Map<String, Object>> calculateRecipePrice(
            @PathVariable Long id,
            @RequestParam(defaultValue = "1") Integer servings) {
        
        return recipeRepository.findById(id)
            .map(recipe -> {
                Map<String, Object> response = new HashMap<>();
                response.put("recipeId", recipe.getId());
                response.put("recipeTitle", recipe.getTitle());
                response.put("originalServings", recipe.getServings());
                response.put("requestedServings", servings);
                response.put("pricePerServing", recipe.getTotalPrice() / recipe.getServings());
                response.put("totalPrice", recipe.getTotalPriceForServings(servings));
                
                return ResponseEntity.ok(response);
            })
            .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Obtenir les statistiques des recettes
     */
    @GetMapping("/stats")
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public ResponseEntity<Map<String, Object>> getRecipeStats(
            @RequestHeader("Authorization") String jwt) throws Exception {
        
        User user = userService.findUserByJwtToken(jwt);
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalRecipes", recipeRepository.countByUser(user));
        stats.put("publishedRecipes", recipeRepository.countByIsPublishedTrue());
        
        return ResponseEntity.ok(stats);
    }
}
