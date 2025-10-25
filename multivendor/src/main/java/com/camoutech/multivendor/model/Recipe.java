package com.camoutech.multivendor.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Modèle pour les recettes de cuisine
 */
@Entity
@Table(name = "recipes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"user", "ingredients"})
public class Recipe {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(columnDefinition = "TEXT", nullable = false)
    private String instructions;
    
    @Column(nullable = false)
    private Integer servings = 1; // Nombre de portions par défaut
    
    @Column(nullable = false)
    private Integer preparationTime; // Temps de préparation en minutes
    
    @Column(nullable = false)
    private Integer cookingTime; // Temps de cuisson en minutes
    
    @Column(nullable = false)
    private String difficulty; // FACILE, MOYEN, DIFFICILE
    
    @Column(nullable = false)
    private String category; // ENTREE, PLAT, DESSERT, BOISSON
    
    @Column(nullable = false)
    private Boolean isPublished = false; // Recette publique ou privée
    
    @Column(nullable = false)
    private Integer totalPrice = 0; // Prix total de la recette en FCFA
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user; // Créateur de la recette
    
    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<RecipeIngredient> ingredients = new ArrayList<>();
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Méthodes utilitaires
    public void addIngredient(RecipeIngredient ingredient) {
        ingredients.add(ingredient);
        ingredient.setRecipe(this);
    }
    
    public void removeIngredient(RecipeIngredient ingredient) {
        ingredients.remove(ingredient);
        ingredient.setRecipe(null);
    }
    
    public void calculateTotalPrice() {
        if (ingredients == null || ingredients.isEmpty()) {
            totalPrice = 0;
            return;
        }
        
        totalPrice = ingredients.stream()
            .filter(ingredient -> ingredient.getProduct() != null)
            .mapToInt(ingredient -> {
                try {
                    return ingredient.getQuantity() * ingredient.getProduct().getSellingPrice();
                } catch (Exception e) {
                    return 0;
                }
            })
            .sum();
    }
    
    public Integer getTotalPriceForServings(Integer numberOfServings) {
        return (totalPrice * numberOfServings) / servings;
    }
}
