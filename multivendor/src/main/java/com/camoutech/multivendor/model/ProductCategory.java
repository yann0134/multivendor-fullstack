package com.camoutech.multivendor.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Catégorie de produits agricoles (Animaux ou Végétaux)
 */
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductCategory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    private String name; // Ex: "Légumes", "Fruits", "Viandes", "Produits laitiers"
    
    private String description;
    
    @Enumerated(EnumType.STRING)
    private CategoryType type; // ANIMAL ou VEGETAL
    
    private String icon; // Icône pour l'interface
    
    private String color; // Couleur de la catégorie
    
    private boolean isActive = true;
    
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Product> products = new ArrayList<>();
    
    @OneToMany(mappedBy = "parentCategory", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductSubCategory> subCategories = new ArrayList<>();
    
    public enum CategoryType {
        ANIMAL("Produits d'origine animale"),
        VEGETAL("Produits d'origine végétale");
        
        private final String description;
        
        CategoryType(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
}
