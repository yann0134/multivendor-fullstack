package com.camoutech.multivendor.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO pour les plannings de repas
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MealPlanDTO {
    
    private Long id;
    private String name;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer totalDays;
    private Integer servingsPerMeal;
    private Integer totalPrice;
    private Integer minPricePerRecipe;
    private String mealType;
    private List<MealPlanItemDTO> items;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
