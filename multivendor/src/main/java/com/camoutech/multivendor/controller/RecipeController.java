package com.camoutech.multivendor.controller;

import com.camoutech.multivendor.dto.CreateRecipeIngredientRequest;
import com.camoutech.multivendor.dto.CreateRecipeRequest;
import com.camoutech.multivendor.dto.RecipeDTO;
import com.camoutech.multivendor.model.Product;
import com.camoutech.multivendor.model.Recipe;
import com.camoutech.multivendor.model.RecipeIngredient;
import com.camoutech.multivendor.model.User;
import com.camoutech.multivendor.repository.RecipeIngredientRepository;
import com.camoutech.multivendor.repository.RecipeRepository;
import com.camoutech.multivendor.service.ProductService;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Contrôleur pour la gestion des recettes
 */
@RestController
@RequestMapping("/api/recipes")
@RequiredArgsConstructor
public class RecipeController {
    
    private final RecipeRepository recipeRepository;
    private final RecipeIngredientRepository recipeIngredientRepository;
    private final UserService userService;
    private final RecipeDTOConverter dtoConverter;
    private final ProductService productService;
    
    /**
     * Créer une nouvelle recette
     */
    @PostMapping
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public ResponseEntity<RecipeDTO> createRecipe(
            @RequestBody CreateRecipeRequest request,
            @RequestHeader("Authorization") String jwt) throws Exception {
        
        // Validation des champs requis
        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        if (request.getInstructions() == null || request.getInstructions().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        if (request.getServings() == null || request.getServings() <= 0) {
            request.setServings(1);
        }
        
        if (request.getPreparationTime() == null || request.getPreparationTime() <= 0) {
            return ResponseEntity.badRequest().build();
        }
        
        if (request.getCookingTime() == null || request.getCookingTime() < 0) {
            request.setCookingTime(0);
        }
        
        if (request.getDifficulty() == null || request.getDifficulty().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        if (request.getCategory() == null || request.getCategory().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        User user = userService.findUserByJwtToken(jwt);
        
        // Créer l'entité Recipe
        Recipe recipe = new Recipe();
        recipe.setTitle(request.getTitle());
        recipe.setDescription(request.getDescription());
        recipe.setInstructions(request.getInstructions());
        recipe.setServings(request.getServings());
        recipe.setPreparationTime(request.getPreparationTime());
        recipe.setCookingTime(request.getCookingTime());
        recipe.setDifficulty(request.getDifficulty());
        recipe.setCategory(request.getCategory());
        recipe.setIsPublished(request.getIsPublished());
        recipe.setUser(user);
        
        // Traiter les ingrédients
        if (request.getIngredients() != null && !request.getIngredients().isEmpty()) {
            List<RecipeIngredient> validIngredients = new ArrayList<>();
            Set<Long> addedProductIds = new HashSet<>(); // Pour éviter les doublons
            
            for (CreateRecipeIngredientRequest ingredientRequest : request.getIngredients()) {
                if (ingredientRequest.getProductId() != null) {
                    // Vérifier si ce produit n'a pas déjà été ajouté
                    if (addedProductIds.contains(ingredientRequest.getProductId())) {
                        continue; // Ignorer les doublons
                    }
                    
                    // Récupérer le produit depuis la base de données
                    Product product = productService.findProductById(ingredientRequest.getProductId());
                    if (product != null) {
                        // Créer un nouvel ingrédient avec le produit récupéré
                        RecipeIngredient newIngredient = new RecipeIngredient();
                        newIngredient.setProduct(product);
                        newIngredient.setQuantity(ingredientRequest.getQuantity());
                        newIngredient.setUnit(ingredientRequest.getUnit());
                        newIngredient.setNotes(ingredientRequest.getNotes());
                        newIngredient.setRecipe(recipe);
                        validIngredients.add(newIngredient);
                        
                        // Marquer ce produit comme ajouté
                        addedProductIds.add(ingredientRequest.getProductId());
                    }
                }
            }
            
            // Ajouter les ingrédients à la recette
            recipe.getIngredients().addAll(validIngredients);
        }
        
        // Recalculer le prix total après avoir ajouté les ingrédients
        recipe.calculateTotalPrice();
        
        // Sauvegarder la recette (les ingrédients seront sauvegardés automatiquement grâce à CascadeType.ALL)
        Recipe savedRecipe = recipeRepository.save(recipe);
        
        // Recalculer le prix total après la sauvegarde pour s'assurer qu'il est à jour
        savedRecipe.calculateTotalPrice();
        savedRecipe = recipeRepository.save(savedRecipe);
        
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
            // Recalculer le prix total pour s'assurer qu'il est à jour
            recipe.calculateTotalPrice();
            return ResponseEntity.ok(dtoConverter.toDTO(recipe));
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Récupérer une recette par ID pour l'édition (avec tous les détails)
     */
    @GetMapping("/{id}/edit")
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public ResponseEntity<RecipeDTO> getRecipeForEdit(
            @PathVariable Long id,
            @RequestHeader("Authorization") String jwt) throws Exception {
        
        User user = userService.findUserByJwtToken(jwt);
        
        Recipe recipe = recipeRepository.findByIdWithIngredientsAndProducts(id);
        if (recipe == null) {
            recipe = recipeRepository.findById(id).orElse(null);
        }
        
        if (recipe == null) {
            return ResponseEntity.notFound().build();
        }
        
        // Vérifier que l'utilisateur est le propriétaire de la recette
        if (!recipe.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        // Recalculer le prix total pour s'assurer qu'il est à jour
        recipe.calculateTotalPrice();
        
        return ResponseEntity.ok(dtoConverter.toDTO(recipe));
    }
    
    /**
     * Mettre à jour une recette
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public ResponseEntity<RecipeDTO> updateRecipe(
            @PathVariable Long id,
            @RequestBody CreateRecipeRequest request,
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
        
        // Validation des champs requis
        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        if (request.getInstructions() == null || request.getInstructions().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        if (request.getServings() == null || request.getServings() <= 0) {
            request.setServings(1); // Default to 1 if invalid
        }
        
        if (request.getPreparationTime() == null || request.getPreparationTime() <= 0) {
            return ResponseEntity.badRequest().build();
        }
        
        if (request.getCookingTime() == null || request.getCookingTime() < 0) {
            request.setCookingTime(0); // Default to 0 if invalid
        }
        
        if (request.getDifficulty() == null || request.getDifficulty().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        if (request.getCategory() == null || request.getCategory().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        // Mettre à jour les informations de base
        existingRecipe.setTitle(request.getTitle());
        existingRecipe.setDescription(request.getDescription());
        existingRecipe.setInstructions(request.getInstructions());
        existingRecipe.setServings(request.getServings());
        existingRecipe.setPreparationTime(request.getPreparationTime());
        existingRecipe.setCookingTime(request.getCookingTime());
        existingRecipe.setDifficulty(request.getDifficulty());
        existingRecipe.setCategory(request.getCategory());
        existingRecipe.setIsPublished(request.getIsPublished());
        
        // Mettre à jour les ingrédients de manière intelligente
        if (request.getIngredients() != null && !request.getIngredients().isEmpty()) {
            // Créer une map des ingrédients existants par productId pour faciliter la recherche
            Map<Long, RecipeIngredient> existingIngredientsMap = new HashMap<>();
            for (RecipeIngredient existingIngredient : existingRecipe.getIngredients()) {
                if (existingIngredient.getProduct() != null) {
                    existingIngredientsMap.put(existingIngredient.getProduct().getId(), existingIngredient);
                }
            }
            
            // Collecter les productIds de la requête pour identifier les ingrédients à conserver
            Set<Long> requestedProductIds = new HashSet<>();
            for (CreateRecipeIngredientRequest ingredientRequest : request.getIngredients()) {
                if (ingredientRequest.getProductId() != null) {
                    requestedProductIds.add(ingredientRequest.getProductId());
                }
            }
            
            // Supprimer les ingrédients qui ne sont plus dans la requête
            List<RecipeIngredient> ingredientsToRemove = new ArrayList<>();
            for (RecipeIngredient ingredient : existingRecipe.getIngredients()) {
                if (ingredient.getProduct() != null && 
                    !requestedProductIds.contains(ingredient.getProduct().getId())) {
                    ingredientsToRemove.add(ingredient);
                }
            }
            
            // Supprimer les ingrédients identifiés de la base de données
            for (RecipeIngredient ingredientToRemove : ingredientsToRemove) {
                existingRecipe.getIngredients().remove(ingredientToRemove);
                // Supprimer explicitement de la base de données
                recipeIngredientRepository.delete(ingredientToRemove);
            }
            
            // Traiter chaque ingrédient de la requête
            for (CreateRecipeIngredientRequest ingredientRequest : request.getIngredients()) {
                if (ingredientRequest.getProductId() != null) {
                    // Récupérer le produit depuis la base de données
                    Product product = productService.findProductById(ingredientRequest.getProductId());
                    if (product != null) {
                        // Vérifier si cet ingrédient existe déjà
                        RecipeIngredient existingIngredient = existingIngredientsMap.get(ingredientRequest.getProductId());
                        
                        if (existingIngredient != null) {
                            // Mettre à jour l'ingrédient existant
                            existingIngredient.setQuantity(ingredientRequest.getQuantity());
                            existingIngredient.setUnit(ingredientRequest.getUnit());
                            existingIngredient.setNotes(ingredientRequest.getNotes());
                        } else {
                            // Créer un nouvel ingrédient seulement s'il n'existe pas déjà
                            RecipeIngredient newIngredient = new RecipeIngredient();
                            newIngredient.setProduct(product);
                            newIngredient.setQuantity(ingredientRequest.getQuantity());
                            newIngredient.setUnit(ingredientRequest.getUnit());
                            newIngredient.setNotes(ingredientRequest.getNotes());
                            newIngredient.setRecipe(existingRecipe);
                            existingRecipe.getIngredients().add(newIngredient);
                        }
                    }
                }
            }
        } else {
            // Si aucun ingrédient n'est fourni, supprimer tous les ingrédients existants
            List<RecipeIngredient> allIngredients = new ArrayList<>(existingRecipe.getIngredients());
            for (RecipeIngredient ingredient : allIngredients) {
                existingRecipe.getIngredients().remove(ingredient);
                recipeIngredientRepository.delete(ingredient);
            }
        }
        
        // Recalculer le prix total
        existingRecipe.calculateTotalPrice();
        
        // Sauvegarder la recette mise à jour
        Recipe updatedRecipe = recipeRepository.save(existingRecipe);
        
        // Recalculer le prix total après la sauvegarde pour s'assurer qu'il est à jour
        updatedRecipe.calculateTotalPrice();
        updatedRecipe = recipeRepository.save(updatedRecipe);
        
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
