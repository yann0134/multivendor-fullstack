package com.camoutech.multivendor.service;

import com.camoutech.multivendor.model.MealPlan;
import com.camoutech.multivendor.model.User;
import org.springframework.data.domain.Page;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Service pour la gestion du planning de repas
 */
public interface MealPlanningService {

    /**
     * Créer un nouveau planning de repas
     */
    MealPlan createMealPlan(MealPlan mealPlan, User user);

    /**
     * Récupérer un planning par ID
     */
    MealPlan findMealPlanById(Long mealPlanId);

    /**
     * Récupérer les plannings d'un utilisateur
     */
    Page<MealPlan> findUserMealPlans(User user, int page, int size);

    /**
     * Récupérer tous les plannings d'un utilisateur
     */
    List<MealPlan> findUserMealPlansList(User user);

    /**
     * Mettre à jour un planning
     */
    MealPlan updateMealPlan(Long mealPlanId, MealPlan updatedMealPlan, User user);

    /**
     * Supprimer un planning
     */
    void deleteMealPlan(Long mealPlanId, User user);

    /**
     * Générer automatiquement un planning de repas
     */
    MealPlan generateMealPlan(
        String name,
        String description,
        Integer numberOfDays,
        Integer servingsPerMeal,
        Integer minPricePerRecipe,
        String mealType,
        User user
    );

    /**
     * Calculer le résumé des produits pour un planning
     */
    Map<String, Object> calculateProductSummary(MealPlan mealPlan);

    /**
     * Ajouter les produits d'un planning au panier
     */
    void addMealPlanToCart(Long mealPlanId, User user);

    /**
     * Obtenir les statistiques d'un planning
     */
    Map<String, Object> getMealPlanStats(MealPlan mealPlan);
}
