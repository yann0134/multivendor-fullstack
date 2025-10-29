package com.camoutech.multivendor.request;

import lombok.Data;

@Data
public class GenerateMealPlanRequest {
    private String name;
    private String description;
    private Integer numberOfDays;
    private Integer servingsPerMeal;
    private Integer minPricePerRecipe;
    private String mealType;
}
