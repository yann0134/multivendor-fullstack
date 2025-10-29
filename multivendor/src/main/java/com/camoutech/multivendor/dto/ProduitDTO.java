package com.camoutech.multivendor.dto;

import com.camoutech.multivendor.model.Product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProduitDTO {
    private Long id;
    private String title;
    private String description;
    private int mrpPrice;
    private int sellingPrice;
    private int discountPercent;
    private int quantity;
    private String color;
    private String categoryName;
    private String sellerName;
    private String nutritionalInfo;

    // Constructeur depuis Product
    public ProduitDTO(Product product) {
        this.id = product.getId();
        this.title = product.getTitle();
        this.description = product.getDescription();
        this.mrpPrice = product.getMrpPrice();
        this.sellingPrice = product.getSellingPrice();
        this.discountPercent = product.getDiscountPercent();
        this.quantity = product.getQuantity();
        this.color = product.getColor();
        this.categoryName = product.getCategory() != null ? product.getCategory().getName() : null;
        this.sellerName = product.getSeller() != null ? product.getSeller().getSellerName() : null;
        this.nutritionalInfo = product.getNutritionalInfo();
    }
}