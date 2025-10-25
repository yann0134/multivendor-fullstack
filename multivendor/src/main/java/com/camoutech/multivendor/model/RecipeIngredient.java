package com.camoutech.multivendor.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Modèle pour les ingrédients d'une recette
 */
@Entity
@Table(name = "recipe_ingredients")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"recipe", "product"})
public class RecipeIngredient {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id", nullable = false)
    @JsonBackReference
    private Recipe recipe;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    @JsonIgnore
    private Product product;
    
    @Column(nullable = false)
    private Integer quantity; // Quantité nécessaire
    
    @Column(nullable = false)
    private String unit; // Unité de mesure (kg, g, L, ml, pièce, etc.)
    
    @Column(columnDefinition = "TEXT")
    private String notes; // Notes optionnelles sur l'ingrédient
    
    // Méthodes utilitaires
    public Integer getTotalPrice() {
        return quantity * product.getSellingPrice();
    }
}
