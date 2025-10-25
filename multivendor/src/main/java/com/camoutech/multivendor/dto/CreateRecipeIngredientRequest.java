package com.camoutech.multivendor.dto;

import lombok.Data;

@Data
public class CreateRecipeIngredientRequest {
    private Long productId;
    private Integer quantity;
    private String unit;
    private String notes;
}
