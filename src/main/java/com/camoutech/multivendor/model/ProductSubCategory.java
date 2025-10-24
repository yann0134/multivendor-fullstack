package com.camoutech.multivendor.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Sous-catégorie de produits agricoles
 */
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductSubCategory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    private String name; // Ex: "Tomates", "Pommes", "Bœuf", "Lait"
    
    private String description;
    
    @ManyToOne
    @JoinColumn(name = "parent_category_id")
    private ProductCategory parentCategory;
    
    private String icon;
    
    private boolean isActive = true;
    
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @OneToMany(mappedBy = "subCategory", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Product> products = new ArrayList<>();
}
