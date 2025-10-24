/**
 * Created by camoutech
 * Date :17/10/2024
 * Time :17:45
 * Project Name :multivendor
 */

package com.camoutech.multivendor.request;

import lombok.Data;

@Data
public class CreateProductRequest {
    private String title;
    private String description;
    private int mrpPrice;
    private int sellingPrice;
    private int stockQuantity = 0;
    private int supplierAvailableQuantity = 0; // Quantité disponible chez le fournisseur
    private int adminRequestedQuantity = 0; // Quantité demandée par l'admin
    private String color;
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
    private Boolean organic = false;
    private Boolean local = false;
    private Boolean fresh = false;
}
