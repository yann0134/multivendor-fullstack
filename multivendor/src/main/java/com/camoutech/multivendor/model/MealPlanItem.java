package com.camoutech.multivendor.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

/**
 * Modèle pour les items d'un planning de repas
 */
@Entity
@Table(name = "meal_plan_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"mealPlan", "recipe"})
public class MealPlanItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meal_plan_id", nullable = false)
    @JsonBackReference
    private MealPlan mealPlan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id", nullable = false)
    @JsonIgnore
    private Recipe recipe;

    @Column(nullable = false)
    private LocalDate mealDate;

    @Column(nullable = false)
    private String mealType; // petit-dejeuner, dejeuner, diner, snack

    @Column(nullable = false)
    private Integer servings;

    @Column(nullable = false)
    private Integer priceForServings;

    /**
     * Calculer le prix pour le nombre de portions spécifié
     */
    public void calculatePriceForServings() {
        if (recipe != null && servings != null) {
            priceForServings = recipe.getTotalPriceForServings(servings);
        }
    }
}
