package com.camoutech.multivendor.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Modèle pour le planning de repas
 */
@Entity
@Table(name = "meal_plans")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"user", "items"})
public class MealPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column(nullable = false)
    private Integer totalDays;

    @Column(nullable = false)
    private Integer servingsPerMeal;

    @Column(nullable = false)
    private Integer totalPrice;

    @Column(nullable = false)
    private Integer minPricePerRecipe;

    @Column(nullable = false)
    private String mealType; // petit-dejeuner, dejeuner, diner, snack

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    @OneToMany(mappedBy = "mealPlan", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OrderBy("mealDate ASC, mealType ASC")
    private List<MealPlanItem> items = new ArrayList<>();

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * Ajouter un item au planning
     */
    public void addItem(MealPlanItem item) {
        items.add(item);
        item.setMealPlan(this);
    }

    /**
     * Supprimer un item du planning
     */
    public void removeItem(MealPlanItem item) {
        items.remove(item);
        item.setMealPlan(null);
    }

    /**
     * Calculer le prix total du planning
     */
    public void calculateTotalPrice() {
        if (items == null || items.isEmpty()) {
            totalPrice = 0;
            return;
        }

        totalPrice = items.stream()
            .filter(item -> item.getRecipe() != null)
            .mapToInt(item -> {
                try {
                    return item.getRecipe().getTotalPriceForServings(servingsPerMeal);
                } catch (Exception e) {
                    return 0;
                }
            })
            .sum();
    }

    /**
     * Obtenir le nombre total de repas
     */
    public int getTotalMeals() {
        return totalDays * getMealsPerDay();
    }

    /**
     * Obtenir le nombre de repas par jour selon le type
     */
    public int getMealsPerDay() {
        switch (mealType.toLowerCase()) {
            case "petit-dejeuner":
            case "dejeuner":
            case "diner":
                return 1;
            case "snack":
                return 2; // 2 collations par jour
            default:
                return 1;
        }
    }
}
