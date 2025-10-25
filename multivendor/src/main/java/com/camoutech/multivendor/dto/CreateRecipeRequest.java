package com.camoutech.multivendor.dto;

import lombok.Data;

import java.util.List;

@Data
public class CreateRecipeRequest {
    private String title;
    private String description;
    private String instructions;
    private Integer servings;
    private Integer preparationTime;
    private Integer cookingTime;
    private String difficulty;
    private String category;
    private String imageUrl;
    private Boolean isPublished;
    private List<CreateRecipeIngredientRequest> ingredients;
}
