package com.camoutech.multivendor.repository;

import com.camoutech.multivendor.model.MealPlan;
import com.camoutech.multivendor.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository pour les plannings de repas
 */
@Repository
public interface MealPlanRepository extends JpaRepository<MealPlan, Long> {

    /**
     * Récupérer les plannings d'un utilisateur
     */
    Page<MealPlan> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);

    /**
     * Récupérer les plannings d'un utilisateur par ID
     */
    List<MealPlan> findByUser_IdOrderByCreatedAtDesc(Long userId);

    /**
     * Récupérer un planning avec ses items (sans les ingrédients des recettes)
     */
    @Query("SELECT DISTINCT mp FROM MealPlan mp LEFT JOIN FETCH mp.items WHERE mp.id = :id")
    MealPlan findByIdWithItems(@Param("id") Long id);

    /**
     * Récupérer les plannings actifs (dans la période)
     */
    @Query("SELECT mp FROM MealPlan mp WHERE mp.user = :user AND mp.startDate <= :date AND mp.endDate >= :date ORDER BY mp.startDate ASC")
    List<MealPlan> findActivePlansByUserAndDate(@Param("user") User user, @Param("date") LocalDate date);

    /**
     * Compter les plannings d'un utilisateur
     */
    Long countByUser(User user);
}
