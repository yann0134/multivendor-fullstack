/**
 * Created by camoutech
 * Date :17/10/2024
 * Time :17:45
 * Project Name :multivendor
 */

package com.camoutech.multivendor.request;

import lombok.Data;

import java.util.List;

@Data
public class CreateProductRequest {
    private String title;
    private String description;
    private int mrpPrice;
    private int sellingPrice;
    private int stockQuantity = 0;
    private String color;
    private List<String> images;
    private String category;
    private String category2;
    private String category3;
    private String sizes;
    
    // Informations agricoles
    private String origin;
    private String farmingMethod;
    private String season;
    private String unit;
    private Double weight;
    private String storageConditions;
    private String nutritionalInfo;
    private String allergens;
    private Boolean organic;
    private Boolean local;
    private Boolean fresh;
}
