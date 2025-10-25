package com.camoutech.multivendor.repository;

import com.camoutech.multivendor.model.MealPlanItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository pour les items de planning de repas
 */
@Repository
public interface MealPlanItemRepository extends JpaRepository<MealPlanItem, Long> {

    /**
     * Récupérer les items d'un planning
     */
    List<MealPlanItem> findByMealPlan_IdOrderByMealDateAscMealTypeAsc(Long mealPlanId);

    /**
     * Récupérer les items d'un planning pour une date spécifique
     */
    List<MealPlanItem> findByMealPlan_IdAndMealDateOrderByMealTypeAsc(Long mealPlanId, LocalDate mealDate);

    /**
     * Récupérer les items d'un planning pour un type de repas spécifique
     */
    List<MealPlanItem> findByMealPlan_IdAndMealTypeOrderByMealDateAsc(Long mealPlanId, String mealType);

    /**
     * Supprimer les items d'un planning
     */
    void deleteByMealPlan_Id(Long mealPlanId);
}
