package com.camoutech.multivendor.service.impl;

import com.camoutech.multivendor.model.*;
import com.camoutech.multivendor.repository.MealPlanItemRepository;
import com.camoutech.multivendor.repository.MealPlanRepository;
import com.camoutech.multivendor.repository.RecipeRepository;
import com.camoutech.multivendor.service.MealPlanningService;
import com.camoutech.multivendor.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implémentation du service de planning de repas
 */
@Service
@RequiredArgsConstructor
public class MealPlanningServiceImpl implements MealPlanningService {

    private final MealPlanRepository mealPlanRepository;
    private final MealPlanItemRepository mealPlanItemRepository;
    private final RecipeRepository recipeRepository;
    private final CartService cartService;

    @Override
    public MealPlan createMealPlan(MealPlan mealPlan, User user) {
        System.out.println("🔄 Création du planning:");
        System.out.println("- Nom: " + mealPlan.getName());
        System.out.println("- StartDate: " + mealPlan.getStartDate());
        System.out.println("- EndDate: " + mealPlan.getEndDate());
        System.out.println("- TotalDays: " + mealPlan.getTotalDays());
        
        mealPlan.setUser(user);
        
        // Calculer les dates si elles ne sont pas définies
        if (mealPlan.getStartDate() == null) {
            mealPlan.setStartDate(LocalDate.now());
            System.out.println("📅 StartDate définie à: " + mealPlan.getStartDate());
        }
        
        // Définir une valeur par défaut pour totalDays si elle est null
        if (mealPlan.getTotalDays() == null) {
            mealPlan.setTotalDays(1); // Valeur par défaut : 1 jour
            System.out.println("📅 TotalDays définie à: " + mealPlan.getTotalDays());
        }
        
        if (mealPlan.getEndDate() == null) {
            mealPlan.setEndDate(mealPlan.getStartDate().plusDays(mealPlan.getTotalDays() - 1));
            System.out.println("📅 EndDate calculée à: " + mealPlan.getEndDate());
        }
        
        System.out.println("✅ Dates finales - Start: " + mealPlan.getStartDate() + ", End: " + mealPlan.getEndDate());
        
        mealPlan.calculateTotalPrice();
        return mealPlanRepository.save(mealPlan);
    }

    @Override
    public MealPlan findMealPlanById(Long mealPlanId) {
        return mealPlanRepository.findByIdWithItems(mealPlanId);
    }

    @Override
    public Page<MealPlan> findUserMealPlans(User user, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return mealPlanRepository.findByUserOrderByCreatedAtDesc(user, pageable);
    }

    @Override
    public List<MealPlan> findUserMealPlansList(User user) {
        return mealPlanRepository.findByUser_IdOrderByCreatedAtDesc(user.getId());
    }

    @Override
    public MealPlan updateMealPlan(Long mealPlanId, MealPlan updatedMealPlan, User user) {
        MealPlan existingMealPlan = findMealPlanById(mealPlanId);
        if (existingMealPlan == null || !existingMealPlan.getUser().getId().equals(user.getId())) {
            return null;
        }

        existingMealPlan.setName(updatedMealPlan.getName());
        existingMealPlan.setDescription(updatedMealPlan.getDescription());
        existingMealPlan.setStartDate(updatedMealPlan.getStartDate());
        existingMealPlan.setEndDate(updatedMealPlan.getEndDate());
        existingMealPlan.setTotalDays(updatedMealPlan.getTotalDays());
        existingMealPlan.setServingsPerMeal(updatedMealPlan.getServingsPerMeal());
        existingMealPlan.setMinPricePerRecipe(updatedMealPlan.getMinPricePerRecipe());
        existingMealPlan.setMealType(updatedMealPlan.getMealType());

        existingMealPlan.calculateTotalPrice();
        return mealPlanRepository.save(existingMealPlan);
    }

    @Override
    public void deleteMealPlan(Long mealPlanId, User user) {
        MealPlan mealPlan = findMealPlanById(mealPlanId);
        if (mealPlan != null && mealPlan.getUser().getId().equals(user.getId())) {
            mealPlanRepository.delete(mealPlan);
        }
    }

    @Override
    public MealPlan generateMealPlan(
            String name,
            String description,
            Integer numberOfDays,
            Integer servingsPerMeal,
            Integer minPricePerRecipe,
            String mealType,
            User user) {

        // Créer le planning avec la date d'aujourd'hui comme début
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(numberOfDays - 1);

        System.out.println("🔄 Génération du planning:");
        System.out.println("- Nom: " + name);
        System.out.println("- Nombre de jours: " + numberOfDays);
        System.out.println("- Date de début: " + startDate);
        System.out.println("- Date de fin: " + endDate);

        MealPlan mealPlan = new MealPlan();
        mealPlan.setName(name);
        mealPlan.setDescription(description);
        mealPlan.setStartDate(startDate);
        mealPlan.setEndDate(endDate);
        mealPlan.setTotalDays(numberOfDays);
        mealPlan.setServingsPerMeal(servingsPerMeal);
        mealPlan.setMinPricePerRecipe(minPricePerRecipe);
        mealPlan.setMealType(mealType);
        mealPlan.setUser(user);

        // Récupérer les recettes disponibles selon les critères
        List<Recipe> availableRecipes = getAvailableRecipes(minPricePerRecipe, mealType);

        if (availableRecipes.isEmpty()) {
            System.out.println("⚠️ Aucune recette trouvée avec les critères stricts, essai avec critères plus larges...");
            // Essayer avec des critères plus larges
            availableRecipes = getAvailableRecipes(0, null); // Aucun filtre
            
            if (availableRecipes.isEmpty()) {
                throw new RuntimeException("Aucune recette disponible dans la base de données");
            }
        }

        // Générer les items du planning
        generateMealPlanItems(mealPlan, availableRecipes, startDate, numberOfDays, servingsPerMeal);

        // Calculer le prix total
        mealPlan.calculateTotalPrice();

        return mealPlanRepository.save(mealPlan);
    }

    @Override
    public Map<String, Object> calculateProductSummary(MealPlan mealPlan) {
        Map<String, Object> summary = new HashMap<>();
        Map<Long, Integer> productQuantities = new HashMap<>();
        Map<Long, String> productNames = new HashMap<>();
        Map<Long, Integer> productPrices = new HashMap<>();
        int totalPrice = 0;

        // Parcourir tous les items du planning
        for (MealPlanItem item : mealPlan.getItems()) {
            if (item.getRecipe() != null) {
                // Parcourir les ingrédients de chaque recette
                for (RecipeIngredient ingredient : item.getRecipe().getIngredients()) {
                    if (ingredient.getProduct() != null) {
                        Long productId = ingredient.getProduct().getId();
                        String productName = ingredient.getProduct().getTitle();
                        Integer productPrice = ingredient.getProduct().getSellingPrice();

                        // Calculer la quantité ajustée pour le nombre de portions
                        int adjustedQuantity = Math.round((ingredient.getQuantity() * item.getServings()) / item.getRecipe().getServings());

                        // Regrouper les quantités par produit
                        productQuantities.merge(productId, adjustedQuantity, Integer::sum);
                        productNames.put(productId, productName);
                        productPrices.put(productId, productPrice);
                    }
                }
            }
        }

        // Calculer le prix total
        for (Map.Entry<Long, Integer> entry : productQuantities.entrySet()) {
            Long productId = entry.getKey();
            Integer quantity = entry.getValue();
            Integer price = productPrices.get(productId);
            totalPrice += quantity * price;
        }

        // Créer la liste des produits regroupés
        List<Map<String, Object>> groupedProducts = new ArrayList<>();
        for (Map.Entry<Long, Integer> entry : productQuantities.entrySet()) {
            Long productId = entry.getKey();
            Map<String, Object> productInfo = new HashMap<>();
            productInfo.put("productId", productId);
            productInfo.put("productName", productNames.get(productId));
            productInfo.put("totalQuantity", entry.getValue());
            productInfo.put("unitPrice", productPrices.get(productId));
            productInfo.put("totalPrice", entry.getValue() * productPrices.get(productId));
            groupedProducts.add(productInfo);
        }

        summary.put("groupedProducts", groupedProducts);
        summary.put("totalPrice", totalPrice);
        summary.put("totalProducts", productQuantities.size());

        return summary;
    }

    @Override
    public void addMealPlanToCart(Long mealPlanId, User user) {
        MealPlan mealPlan = findMealPlanById(mealPlanId);
        if (mealPlan == null || !mealPlan.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Planning de repas non trouvé ou non autorisé");
        }

        // Calculer le résumé des produits
        Map<String, Object> productSummary = calculateProductSummary(mealPlan);
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> groupedProducts = (List<Map<String, Object>>) productSummary.get("groupedProducts");

        // Ajouter chaque produit au panier
        for (Map<String, Object> productInfo : groupedProducts) {
            Long productId = (Long) productInfo.get("productId");
            Integer totalQuantity = (Integer) productInfo.get("totalQuantity");

            // Récupérer le produit
            Product product = new Product();
            product.setId(productId);

            // Ajouter au panier
            cartService.addCartItem(user, product, "DEFAULT", totalQuantity);
        }
    }

    @Override
    public Map<String, Object> getMealPlanStats(MealPlan mealPlan) {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalDays", mealPlan.getTotalDays());
        stats.put("totalMeals", mealPlan.getTotalMeals());
        stats.put("servingsPerMeal", mealPlan.getServingsPerMeal());
        stats.put("totalPrice", mealPlan.getTotalPrice());
        stats.put("averagePricePerMeal", mealPlan.getTotalPrice() / mealPlan.getTotalMeals());
        stats.put("mealType", mealPlan.getMealType());
        stats.put("startDate", mealPlan.getStartDate());
        stats.put("endDate", mealPlan.getEndDate());

        return stats;
    }

    /**
     * Récupérer les recettes disponibles selon les critères
     */
    private List<Recipe> getAvailableRecipes(Integer minPricePerRecipe, String mealType) {
        // Récupérer toutes les recettes publiées
        List<Recipe> allRecipes = recipeRepository.findByIsPublishedTrueOrderByCreatedAtDesc();
        
        System.out.println("🔍 Total recettes trouvées: " + allRecipes.size());
        System.out.println("🔍 Critères: minPrice=" + minPricePerRecipe + ", mealType=" + mealType);

        // Filtrer par prix minimum et type de repas
        List<Recipe> filteredRecipes = allRecipes.stream()
            .filter(recipe -> {
                System.out.println("📋 Recette: " + recipe.getTitle() + 
                    " | Prix: " + recipe.getTotalPrice() + 
                    " | Catégorie: " + recipe.getCategory());
                
                // Vérifier le prix minimum (seulement si spécifié et > 0)
                if (minPricePerRecipe != null && minPricePerRecipe > 0) {
                    if (recipe.getTotalPrice() < minPricePerRecipe) {
                        System.out.println("❌ Prix trop bas: " + recipe.getTotalPrice() + " < " + minPricePerRecipe);
                        return false;
                    }
                }

                // Vérifier le type de repas (si spécifié)
                if (mealType != null && !mealType.isEmpty()) {
                    boolean categoryMatch = mealType.equalsIgnoreCase(recipe.getCategory());
                    System.out.println("🍽️ Catégorie match: " + categoryMatch + " (" + mealType + " vs " + recipe.getCategory() + ")");
                    return categoryMatch;
                }

                return true;
            })
            .collect(Collectors.toList());
            
        System.out.println("✅ Recettes filtrées: " + filteredRecipes.size());
        return filteredRecipes;
    }

    /**
     * Générer les items du planning
     */
    private void generateMealPlanItems(MealPlan mealPlan, List<Recipe> availableRecipes, 
                                     LocalDate startDate, Integer numberOfDays, Integer servingsPerMeal) {
        Random random = new Random();
        LocalDate currentDate = startDate;

        System.out.println("🔄 Génération des items du planning...");
        System.out.println("📅 Nombre de jours: " + numberOfDays);
        System.out.println("🍽️ Recettes disponibles: " + availableRecipes.size());

        for (int i = 0; i < numberOfDays; i++) {
            // Sélectionner une recette aléatoire
            Recipe selectedRecipe = availableRecipes.get(random.nextInt(availableRecipes.size()));

            // Créer l'item du planning
            MealPlanItem item = new MealPlanItem();
            item.setMealPlan(mealPlan);
            item.setRecipe(selectedRecipe);
            item.setMealDate(currentDate);
            item.setMealType(mealPlan.getMealType());
            item.setServings(servingsPerMeal);
            item.calculatePriceForServings();

            mealPlan.addItem(item);

            System.out.println("✅ Jour " + (i + 1) + " (" + currentDate + "): " + selectedRecipe.getTitle() + 
                             " - " + servingsPerMeal + " portions - " + item.getPriceForServings() + " FCFA");

            // Passer au jour suivant
            currentDate = currentDate.plusDays(1);
        }

        System.out.println("🎯 Total items générés: " + mealPlan.getItems().size());
    }
}
